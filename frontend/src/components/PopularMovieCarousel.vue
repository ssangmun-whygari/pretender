<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col lg="8" cols="12">
        <v-progress-linear v-if="loading === true" color="primary" indeterminate></v-progress-linear>
        <div v-if="loading === false" class="slider">
          <div class="slide-track">
            <div v-for="(col, index) in groupedIndexes" :key="`col-${index}`" class="slide-column"> <!-- col의 예 : [0, 1], index의 예 : 0 -->
              <div v-for="(item, i) in col" :key="`slide-${index}-${i}`" class="slide"> <!-- item의 예 : 8, i의 예 : 0 -->
                <RouterLink v-bind:to="`/detail?id=${popularMovieInfos[item]['id']}&type=movie`">
                  <img class="slide-image" :src="posterPath(popularMovieInfos[item]['poster_path'])">
                  </img>
                </RouterLink>
              </div>
            </div>
            <div v-for="(col, index) in groupedIndexes" :key="index" class="slide-column"> <!-- col의 예 : [0, 1], index의 예 : 0 -->
              <div v-for="(item, i) in col" :key="i" class="slide"> <!-- item의 예 : 8, i의 예 : 0 -->
                <RouterLink v-bind:to="`/detail?id=${popularMovieInfos[item]['id']}&type=movie`">
                  <img class="slide-image" :src="posterPath(popularMovieInfos[item]['poster_path'])">
                  </img>
                </RouterLink>
              </div>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
  /* 슬라이더 컨테이너 */
  .slider {
    width: 100%;
    overflow: hidden;
  }

  /* 슬라이드 전체를 감싸는 트랙 (왼쪽으로 이동) */
  .slide-track {
    width: 100%;
    display: flex;
    white-space: nowrap;
    animation: slide 10s linear infinite;
    animation-play-state: running;
  }

  .slide-track:hover {
    animation-play-state: paused;
  }

  /* 한 열(column) 단위로 정렬 */
  .slide-column {
    display: flex;
    flex-direction: column;
    width: 15%;
    flex-shrink: 0;
  }

  /* v-sheet 스타일 */
  .slide {
    width: 100%;
    display: block; /* NOTE : inline일 때는 부모 요소의 크기가 아니라 자기 자신의 크기 기준으로 width 비율이 설정됨 */
    padding: 10%; /* NOTE : 브라우저는 가로 패딩, 세로 패딩 모두 width의 10%로 계산함 */
  }

  .slide-image {
    width: 100%;
    aspect-ratio: 0.666;
    display: block; /* NOTE : v-sheet와 img 사이의 작은 간격을 없앨 수 있음 */
  }

  /* 무한 루프를 위한 슬라이드 이동 애니메이션 */
  @keyframes slide {
    0% { transform: translateX(0); }
    100% { transform: translateX(-135%); } /* 15 * 9열 = 135 */
  }
</style>

<script setup>
  import axios from 'axios'
  import { ref, onMounted, watch, nextTick } from 'vue'

  let popularMovieInfos = ref([])
  let posterImageObjects = []
  let posterImageLoadPromises = ref(null)
  const TOTAL_IMAGE_NUMBERS = 18 // 1열에 2개 * 9열
  const posterBaseUrl = "http://image.tmdb.org/t/p/w500/"
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
  let loading = ref(true)
  let groupedIndexes = ref([]) // 예 : [[0, 1], [2, 3], [4, 5], ... [16, 17]]

  let requestPopularMovies = async () => {
    let response = await axios.get(
      'http://localhost:8080/api/popularMovies',
    )
    console.log(response.data.results)
    popularMovieInfos.value = popularMovieInfos.value.concat(response.data.results)
  }
  requestPopularMovies()

  watch(() => {return popularMovieInfos.value.length}, (length) => {
    if (length > 0) { // popularMovieInfos에 값이 채워지면
      posterImageLoadPromises.value = loadPosterImages() // 
    }
  })

  let loadPosterImages = () => {
    for (let i = 0; i < TOTAL_IMAGE_NUMBERS; i++) {
      posterImageObjects.push(new Image())
    }
    let posterImageSources = popularMovieInfos.value.map((info) => {
      return posterPath(info.poster_path)
    }).slice(0, TOTAL_IMAGE_NUMBERS)
    let posterImageSourcesIterator = posterImageSources[Symbol.iterator]() // iterator
    
    return Promise.all(
      posterImageObjects.map((image) => {
        return new Promise((resolve) => {
          if (image.loaded === true) {
            console.log(`${image.src}는 이미 다운로드됨`)
            resolve(image)
          }
          image.src = posterImageSourcesIterator.next().value
          image.onload = () => {
            console.log(`${image.src} 로드 완료`);
            image.loaded = true
            resolve(image);
          };
          image.onerror = () => {
            console.error(`${image.src} 로드 실패`);
            image.loaded = true
            resolve(image);
          };
        }) // new Promise end
      }) // map end
    )
  }

  let posterPath = (string) => {
    if (string) {
      return posterBaseUrl + string 
    } else {
      return noImageUrl
    }
  }

  watch(() => {return posterImageLoadPromises.value}, (promises) => {
    (promises)?.then(() => { // promise들이 다 resolve된 상태라면
      loading.value = false

      let indexes;
      for (let i = 0; i < TOTAL_IMAGE_NUMBERS; i++) {
        if (i % 2 == 0) { indexes = []}
        indexes.push(i)
        if (indexes.length == 2) { groupedIndexes.value.push(indexes) }
      }
      // groupedIndexes의 예 : [[0, 1], [2, 3], [4, 5], ... [16, 17]]
      console.log("======groupedIndexes ...")
      console.log(groupedIndexes.value)
      console.log("======groupedIndexes end")
      nextTick(() => {
        addEventHandler()
      })
    })
  })

  // NOTE
  /*
    .slide-image:hover {
      aspect-ratio: ???;
    }
    이런 식으로 css에서 aspect-ratio를 동적으로 계산하도록 처리하고 싶었지만 안되서 자바스크립트로 처리함
  */
  let addEventHandler = () => {
    document.querySelectorAll('.slide-image').forEach(image => {
      image.addEventListener('mouseenter', () => {
        let width = image.clientWidth;
        let parentWidth = document.querySelector(".slide").clientWidth // 모든 slide의 인스턴스가 동일한 크기라고 가정
        let height = image.clientHeight;
        let parentHeight = document.querySelector(".slide").clientHeight
        /* NOTE : 브라우저는 가로 패딩, 세로 패딩 모두 width의 20%로 계산함 */
        let newAspectRatio = (width + (parentWidth * 0.1 * 2)) / (height + (parentWidth * 0.1 * 2)) // padding이 20%일때
        image.style.aspectRatio = newAspectRatio;
        console.log(`width : ${width}, height: ${height}, parentWidth : ${parentWidth}, parentHeight: ${parentHeight}, newAspectRatio: ${newAspectRatio}`)
        image.parentElement.parentElement.style.padding = "0px" // 중간에 RouterVies element가 끼어있으므로 .parentElement.parentElement로 접근
      });

      image.addEventListener('mouseleave', () => {
        image.style.aspectRatio = '0.666'; // 원래 비율로 복귀
        image.parentElement.parentElement.style.padding = "10%"
      });
    });
  }
</script>