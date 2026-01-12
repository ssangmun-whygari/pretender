<template>
  <v-container fluid class="mediaList">
    <div v-if="imageUrls.allLoaded && lgAndUp" class="progress-image-container">
      <!-- TODO : 계절에 따른 변화 필요 -->
      <img :src="imageCache[apiBaseUrl + '/resource/image?filename=ladder.png']" class="progress-image">
        <img :src="imageCache[apiBaseUrl + '/resource/image?filename=bunny-l.png']" id="progress-image-child"/>
      </img>
    </div>
    <v-row justify="center">
      <v-col lg="8" cols="12">
        <div>
          <span class="mainTitle" @click="onClickCategoryTitle">TV 프로그램</span>
          <span class="altTitle" @click="onClickCategoryTitle">영화</span>
        </div>
        <div v-if="sortMode === 'individual'">
          <div v-for="(item, _) in Object.values(mediaInfoRenderedRef).sort((a, b) => b.year - a.year)">
            <div class="yearCategory"> {{ item.year }}</div>
            <swiper-container
              :key="item.year"
              :attr-year="item.year"
              class="mySwiper mb-3"
              centered-slides="true"
              :breakpoints="{
                '@0.75': {
                  slidesPerView: 2,
                  spaceBetween: 20,
                },
                '@1.00': {
                  slidesPerView: 3,
                  spaceBetween: 40,
                },
                '@1.50': {
                  slidesPerView: 4,
                  spaceBetween: 50,
                },
              }"
              >
              <swiper-slide 
                class="slide"
                v-for="(innerItem, index) in mediaInfoRenderedRef[item.year]['data']"
                >
                <v-skeleton-loader
                  :loading="loading"
                  height="300"
                  type="image"
                  color="transparent"
                >
                  <v-sheet class="ma-3 py-3 w-100 h-100 border rounded-lg poster-image-sheet"
                    :elevation="2">
                    <RouterLink 
                      class="d-flex justify-center h-100 w-100"
                      :to="{path: '/detail', query: {id : innerItem['id'], type : currentCategory}}">
                    <div class="poster-image-container">
                      <img 
                      :src="posterPath(innerItem['poster_path'])"
                      class="poster"
                      @load="onPosterImageLoad"
                      ></img>
                    </div>
                    </RouterLink>
                  </v-sheet>
                </v-skeleton-loader>
              </swiper-slide>
            </swiper-container>

            <v-skeleton-loader
                  :loading="loading"
                  height="50"
                  type="list-item"
                  color="transparent"
            >
              <v-sheet class="d-flex justify-center mb-3 py-3 w-100 border rounded-lg"
                :elevation="2"
                >
                <h2>{{ item['data'][item.activeIndex][getTitlePropertyName()] }}</h2>
              </v-sheet>
            </v-skeleton-loader>
            <v-skeleton-loader
                  :loading="loading"
                  height="50"
                  type="list-item"
                  color="transparent"
            >
              <v-sheet class="marquee-container pa-3 border" :attr-year="item.year">
                <div class="marquee-content" :attr-year="item.year">
                  {{ stripContent(item['data'][item.activeIndex].overview) }}
                </div>
              </v-sheet>
            </v-skeleton-loader>
          </div>
        </div>
      </v-col>
      <v-col lg="1" class="d-none d-lg-block">
      </v-col>
    </v-row>
  </v-container>
</template>

<style>
  .mediaList {
    position: relative;
  }

  .mainTitle {
    font-size: xxx-large;
    font-weight: bold;
    margin-right: 12px;
  }

  .altTitle {
    font-size: xx-large;
    font-weight: bold;
    color: lightgray;
    margin-right: 12px;
  }

  .altTitle:hover {
    font-size: xxx-large;
    font-weight: bold;
    color: lightgray;
    transition: font-size 0.5s ease;
  }

  .v-skeleton-loader__image {
    height: 300px !important;
  }

  .background-container {
    /* background-image: linear-gradient(rgb(255, 255, 255),rgba(255, 255, 255, 0.205)), url('http://localhost:8080/resource/backgroundImage'); */
    background-size: cover;
    background-position: top;
    background-attachment: fixed;
  }

  .progress-image-container {
    position: fixed;
    top: 60%;
    left: 85%;
    transform: translate(-50%, -50%);
  }

  .progress-image {
    position: relative;
    height: 70vh;
  }

  #progress-image-child {
    position: absolute;
    top: 0;
    left: 50%; /* 부모 요소의 너비 기준으로 이미지의 위치를 중앙으로 이동.*/
    height: 25%;
    transform: translateX(-50%); /* 이미지의 기준점을 중앙으로 이동. */
  }

  .yearCategory {
    font-size: 400%;
    font-family: 'PartialSansKR-Regular', sans-serif;
    display: inline-block; /* transform 적용을 위해 필요 */
    transform: scaleX(0.7);
    transform-origin: left;
  }

  .mySwiper {
    width: 100%;
    /* background-color: rgb(250, 239, 175); */
  }

  .slide {
    box-sizing: border-box;
    /* border: 1px solid;
    border-radius: 5%;
    background-color: rgb(185, 185, 255);
    width: 200px; */
  }

  .poster-image-container {
    width: 70%;
    height: 100%;
    overflow: hidden;
  }

  .poster {
    width: 100%;
    object-fit: contain;
    aspect-ratio: 0.666;
  }

  .poster-image-container:hover .poster {
    transition: transform 0.1s linear;
    transform: scale(1.2);
  }

  .poster-image-sheet:hover {
    background-color: #EEEEEE;
  }


  .hidden {
    display: none;
  }

  .marquee-container {
    overflow: hidden;
    white-space: nowrap;
    position: relative;
    height: 50px;
    width: 100%;
  }

  .marquee-content {
    position: absolute;
    /* animation: marquee 30s linear infinite; */
  }

  @keyframes marquee {
    0% {
      transform: translateX(0%);
    }
    80% {
      transform: translateX(var(--translate-end));
    }
    100% {
      transform: translateX(var(--translate-end));
    }
  }

</style>

<script setup>
  import { computed } from 'vue'
  import { register } from 'swiper/element/bundle'
  import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
  import { useDisplay } from 'vuetify'
  const { lgAndUp } = useDisplay()
  register()
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const backgroundImageUrl = apiBaseUrl + '/resource/backgroundImage'
  const YEAR_CATEGORY_NUMBER = 7
  const SCROLL_SPEED = 100
  const itemsPerRow = 3
  const posterBaseUrl = "http://image.tmdb.org/t/p/w500/"
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
  const props = defineProps({
      mediaInfo: Object
    })
  const imageCache = {}; // 스크롤 표시 이미지용(ex : 토끼, 사다리...)
  // NOTE : 이 주소로 GET 요청을 보내 이미지 파일을 받아온다. 
  // 이런 식으로 별도의 배열을 통해 관리해야 배열 안의 이미지가 모두 로딩이 완료되었을 때 스켈레톤 로더를 해제하도록 바꿀 수 있음.
  let posterImageSources = {"tv" : [], "movie" : []} // src들이 저장됨
  let posterImages = {"tv" : [], "movie" : []} // Image 객체들이 저장됨
  const getTitlePropertyName = () => {
    if (currentCategory.value === 'tv') {
      return 'name'
    } else if (currentCategory.value === 'movie') {
      return 'title'
    }
  }
  const getReleasedDatePropertyName = () => {
    if (currentCategory.value === 'tv') {
      return 'first_air_date'
    } else if (currentCategory.value === 'movie') {
      return 'release_date'
    }
  }
  const onClickCategoryTitle = (event) => {
    if (event.target.classList.contains("mainTitle")) {
      // mainTitle을 클릭했을 때는 아무것도 안함
      return
    }
    // altTitle을 클릭했을 때
    let altTitle = event.target
    let mainTitle = document.querySelector(".mainTitle")
    // mainTitle을 altTitle로, altTitle을 mainTitle로 변경
    altTitle.classList.add("mainTitle")
    mainTitle.classList.add("altTitle")
    altTitle.classList.remove("altTitle")
    mainTitle.classList.remove("mainTitle")
    console.log(`currentCategory.value : ${currentCategory.value}`)
    return (currentCategory.value === 'tv') ? currentCategory.value = 'movie' : currentCategory.value = 'tv'
  }

  let loading = ref(true)

  // 이미지 미리 다운로드
  async function preloadImage(url) {
    const response = await fetch(url);
    console.log(response)
    if (!response.ok) throw new Error(`이미지 로드 실패: ${url}`);
    const blob = await response.blob();
    const objectUrl = URL.createObjectURL(blob); // Blob을 Object URL로 변환
    imageCache[url] = objectUrl; // 캐시에 저장
  }
  // 여러 이미지를 사전 로드
  async function preloadImages(imageUrls) {
    for (const url of imageUrls.value.urls) {
      await preloadImage(url);
    }
    imageUrls.value.allLoaded = true // NOTE : 스크롤를 위한 이미지만 포함. 영화등의 포스터 이미지는 추적하지 않음
  }

  // 사전 로드 실행
  const imageUrls = ref({
    urls : [
      apiBaseUrl + '/resource/image?filename=bunny-f.png',
      apiBaseUrl + '/resource/image?filename=bunny-l.png',
      apiBaseUrl + '/resource/image?filename=bunny-r.png',
      apiBaseUrl + '/resource/image?filename=ladder.png',
    ],
    allLoaded : false
  })

  onMounted(() => {
    preloadImages(imageUrls)
    window.addEventListener('scroll', handleScroll)
  });

  onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll);
  });

  // TODO : 매끄러운 스크롤을 위한 해결과정 블로그에 적기
  const handleScroll = (event) => { // event는 브라우저가 나중에 첨부해준다.
    const progressImage = document.querySelector('.progress-image')
    const progressImageChild = document.querySelector('#progress-image-child')
    if (! (progressImage && progressImageChild)) {
      return
    }
    const scrollTop = window.scrollY;
    const docHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
    const scrollPercentage = Math.ceil((scrollTop / docHeight) * 100)

    const height = progressImage.height - progressImageChild.height
    progressImageChild.style.top = `${Math.ceil(scrollPercentage / 100 * height)}px`

    if (scrollPercentage >= 97) {
      progressImageChild.style.top = `${height}px`
      progressImageChild.src = imageCache[apiBaseUrl + '/resource/image?filename=bunny-f.png']
      return
    }

    const ranges_1 = [
      [0, 10], [20, 30], [40, 50], [60, 70], [80, 90]
    ]

    const ranges_2 = [
      [10, 20], [30, 40], [50, 60], [70, 80], [90, 100]
    ]

    for (const [start, end] of ranges_1) {
      if (scrollPercentage >= start && scrollPercentage < end) {
        progressImageChild.src = imageCache[apiBaseUrl + '/resource/image?filename=bunny-l.png']
        return
      }
    }

    for (const [start, end] of ranges_2) {
      if (scrollPercentage >= start && scrollPercentage < end) {
        progressImageChild.src = imageCache[apiBaseUrl + '/resource/image?filename=bunny-r.png']
        return
      }
    }

  } // handleScroll end

  const mediaInfo = computed(() => {
    // NOTE : props.mediaInfo의 정보를 이용해 화면을 구성한다.
    console.log("computed.....")
    console.log(`currentCategory : ${currentCategory.value}`)
    console.log('===return value : ')
    console.log(props.mediaInfo[currentCategory.value])
    console.log('===return value end')
    console.log(props.mediaInfo)
    console.log("computed..... end")
    return props.mediaInfo[currentCategory.value] // currentCategory.value는 'tv'나 'movie' 중 하나
  })

  const renderTriggered = ref(false)
  const currentCategory = ref('tv') // 'tv' or 'movie'
  const initialRender = ref(true) // 처음 렌더링할때만 포스터 이미지들을 다운로드함
  watch(() => {return (currentCategory.value)}, (category) => {
    console.log(`renderTriggered가 true로 변경 (이유 : currentCategory가 ${currentCategory.value}로변경됨)`)
    renderTriggered.value = true
  })
  watch(() => {return props.mediaInfo.allLoaded}, (bool) => {
    if (bool === true) {
      console.log("renderTriggered가 true로 변경 (이유 : mediaInfo에 값이 다 채워짐)")
      renderTriggered.value = true
    }
  })

  // working...
  // TODO : 
  // readyToAttachEventhandler가 바뀌었는 지 감시함
  // readyToAttachEventhandler가  true로 바뀌었다면 이벤트 핸들러를 제거하고 다시 붙임
  // mediaInfoRenderedReady === true이고 preloadImage의 promise가 완료되었는 지 watch()로 감시함
  // readyToAttachEventhandler를 true로 바꿈

  // * mediaInfoRenderedReady를 false로 바꾸는 코드는?
  //   watch(() => {return (currentCategory.value)}, (category) => {
  //     console.log(`renderTriggered가 true로 변경 (이유 : currentCategory가 ${currentCategory.value}로변경됨)`)
  //     renderTriggered.value = true
  //     ===mediaInfoRenderedReady = false===
  //   })
  //   watch(() => {return props.mediaInfo.allLoaded}, (bool) => {
  //     if (bool === true) {
  //       console.log("renderTriggered가 true로 변경 (이유 : mediaInfo에 값이 다 채워짐)")
  //       renderTriggered.value = true
  //       ===mediaInfoRenderedReady = false===
  //     }
  //   })

  // let showByIndividualYears = ref(false) // deprecated
  let sortMode = ref("individual") // individual or category
  // let mediaInfoRendered = {} // deprecated
  let mediaInfoRenderedRef = ref({})
  let preloadPosterPromises = ref(null)


  function preloadPosterImages(sources) {
    // 전역 변수 posterImages의 값을 변경함
    if (posterImages[currentCategory.value].length == 0) {
      for (let i = 0; i < sources.length; i++) {
        posterImages[currentCategory.value].push(new Image())
      }
    }
    let posterImageSourcesIterator = posterImageSources[currentCategory.value][Symbol.iterator]() // iterator
    console.log("==========preloadPosterImages 실행중....==========")
    console.log("posterImages : ")
    console.log(posterImages)
    console.log("posterImages end")
    console.log("==========preloadPosterImages 실행중 end==========")

    return Promise.all(
      posterImages[currentCategory.value].map(image => new Promise(resolve => {
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
      }))
    )
  }

  watch(() => {return preloadPosterPromises.value}, (promises) => {
    // 참고 : https://stackoverflow.com/questions/31013221/typeerror-console-log-is-not-a-function
    console.log("============promises : ");
    console.log(promises);
    console.log(JSON.stringify(promises));
    console.log("============promises end");
    (promises)?.then(() => {
      // console.log(`이미지 : ${posterImageSources.length}개, 모든 포스터 이미지 다운로드 완료`)

      // NOTE : 포스터 이미지의 로딩이 끝나면 loading.value = false가 되므로 스켈레톤 로더가 해제된다.
      loading.value = false; // 모든 포스터 다운로드 완료

      // DOM이 렌더링될떄까지 기다린 다음 swiperslidechange에 이벤트 리스너를 붙인다.
      nextTick(async () => {
        const swipers = document.querySelectorAll('swiper-container')
        // reference : https://inpa.tistory.com/entry/JS-%F0%9F%93%9A-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EC%A0%9C%EA%B1%B0-%ED%95%9C%EB%B2%88%EB%A7%8C-%EC%8B%A4%ED%96%89%EB%90%98%EA%B2%8C-%ED%95%98%EA%B8%B0-removeEventListener-once
        console.log("swipers : ")
        console.log(swipers)
        console.log("swipers end")

        nextTick(() => {
          for (let swiper of swipers) {
            const yearCategory = swiper.getAttribute('attr-year')
            setRenderedOverviewContainerWidth(yearCategory)
            setRenderedOverviewTextWidth(yearCategory)
            // NOTE : <div ... :style="{'--translate-end': getTranslateEnd(item.year) + 'px'}">로 하면  getTranslateEnd()가 setRenderedOverview...보다 빨리 계산되서 안됨
            // 어쩔 수 없이 자바스크립트로 처리함
            const translateEnd = getTranslateEnd(yearCategory)
            const duration = calculateAnimDuration(-translateEnd)
            // getMarqueeContentElement(yearCategory).style.setProperty('--translate-end', 300 + 'px')
            getMarqueeContentElement(yearCategory).style.setProperty('--translate-end', translateEnd + 'px')
            getMarqueeContentElement(yearCategory).style.animation = `marquee ${duration}s linear infinite`;
            getMarqueeContentElement(yearCategory).style.animationDelay = '0s'; // 처음 시간으로 이동
            getMarqueeContentElement(yearCategory).style.animationPlayState = 'running'; // 애니메이션 시작
            swiper.removeEventListener('swiperslidechange', onSwiperslidechange)
            swiper.addEventListener('swiperslidechange', onSwiperslidechange)
          }
          window.addEventListener("resize", onWindowResize)
        }) // nextTick end
      }) // nextTick end
    }) // promises.then() end
  })


  watch(() => {return renderTriggered.value}, (bool) => {
    if (bool === true) {
      renderTriggered.value = false
      loading.value = true
      posterImageSources[currentCategory.value] = (mediaInfo.value).map((e) => {return posterPath(e.poster_path)})
      console.log("posterImageSources : ")
      console.log(posterImageSources)
      console.log(`===currentCategory.value : ${currentCategory.value}`)
      console.log("posterImageSources end")
      preloadPosterPromises.value = preloadPosterImages(posterImageSources[currentCategory.value])
      // NOTE : 
      // let mediaInfoRendered = mediaInfoRendered.value
      // 나는 오른쪽 mediaInfoRendered가 전역변수를 참조하고 mediaInfoRendered는 지역 변수로 새로운 변수가 생기는 줄 알았는데
      // 자바스크립트에서는 둘다 지역변수로 취급됨. 따라서 초기화 에러가 발생함(mediaInfoRendered가 초기화되지 않았는 데 변수를 참조하면서 에러가 생긴다)

      mediaInfo.value = mediaInfo.value.sort((a, b) => {
        let a_date = new Date(a[getReleasedDatePropertyName()])
        let b_date = new Date(b[getReleasedDatePropertyName()])
        return b_date - a_date
      })
      console.log("===mediaInfo.value : ")
      console.log(mediaInfo.value)
      console.log("===mediaInfo.value end")
      let years = mediaInfo.value.map((obj) => { return (new Date(obj[getReleasedDatePropertyName()] )).getFullYear() })
      console.log(`years : `)
      console.log(years)
      let uniqueYears = new Set(years)
      console.log(`unique years : `)
      console.log(uniqueYears)
      mediaInfoRenderedRef.value = {} // mediaInfoRenderedRef 초기화
      let mediaInfoRendered = mediaInfoRenderedRef.value
      if (uniqueYears.size <= 9999) {
        // NOTE : mediaInfo는 computed()에 의해 props에서 계산되어진 속성이다.
        // console.log("=====================mediaInfo.value...")
        // console.log(mediaInfo.value)
        // console.log("=====================mediaInfo.value end")

        // NOTE : preloadPosterImages가 완료되면 setRenderedOverviewTextWidth()등에 의해 renderedOverviewTextWidth에 값이 채워짐
        // 창이 resize될때도 다시 계산됨
        for (let info of mediaInfo.value) {
          let year = `${(new Date( info[getReleasedDatePropertyName()]) ).getFullYear()}`
          // console.log(`year : ${year}`)
          // console.log(typeof year)
          if (typeof year !== "string" || Number.isNaN(Number(year))) {
            continue
          }
          if (!mediaInfoRendered[year]) {
            mediaInfoRendered[year] = {
              data: [], 
              year: year,
              activeIndex: ref(0),
              renderedOverviewTextWidth: 0, 
              renderedOverviewContainerWidth: 0
            }
          }
          mediaInfoRendered[year].data.push(info)
        }
        console.log("=====================mediaInfoRendered...")
        console.log(mediaInfoRendered)
        console.log("=====================mediaInfoRendered end")
      } else {
        // uniqye years가 8개 이상일 때, year를 10년 단위로 그룹을 묶어서 카테고리 개수 줄이기를 시도한다.

      } // if (uniqueYears.size <= 9999) end


    } // if (bool === true) end
  }) // watch() end

/*
  watch(() => {return mediaInfo.value.length}, (length) => {
    posterImageSources = (mediaInfo.value).map((e) => {return posterPath(e.poster_path)})
    // console.log("posterImageSources : ")
    // console.log(posterImageSources)
    // console.log("posterImageSources end")
    preloadPosterPromises.value = preloadPosterImages(posterImageSources)
    // NOTE : 
    // let mediaInfoRendered = mediaInfoRendered.value
    // 나는 오른쪽 mediaInfoRendered가 전역변수를 참조하고 mediaInfoRendered는 지역 변수로 새로운 변수가 생기는 줄 알았는데
    // 자바스크립트에서는 둘다 지역변수로 취급됨. 따라서 초기화 에러가 발생함(mediaInfoRendered가 초기화되지 않았는 데 변수를 참조하면서 에러가 생긴다)

    // mediaInfo에 값이 채워지면 mediaInfoRendered의 값을 만든다.
    // NOTE : mediaInfo에 한번에 값이 채워진다고 가정함(예 : 20개)
    if (length > 0) {
      mediaInfo.value = mediaInfo.value.sort((a, b) => {
        let a_date = new Date(a.first_air_date)
        let b_date = new Date(b.first_air_date)
        return b_date - a_date
      })
      let years = mediaInfo.value.map((obj) => { return (new Date(obj.first_air_date)).getFullYear() })
      console.log(`years : `)
      console.log(years)
      let uniqueYears = new Set(years)
      console.log(`unique years : `)
      console.log(uniqueYears)
      let mediaInfoRendered = mediaInfoRenderedRef.value
      if (uniqueYears.size <= 9999) {
        // console.log("showByIndividualYears : true")
        // showByIndividualYears.value = true // deprecated

        // NOTE : mediaInfo는 computed()에 의해 props에서 계산되어진 속성이다.
        // console.log("=====================mediaInfo.value...")
        // console.log(mediaInfo.value)
        // console.log("=====================mediaInfo.value end")

        // NOTE : preloadPosterImages가 완료되면 setRenderedOverviewTextWidth()등에 의해 renderedOverviewTextWidth에 값이 채워짐
        // 창이 resize될때도 다시 계산됨
        for (let info of mediaInfo.value) {
          let year = `${(new Date(info.first_air_date)).getFullYear()}`
          if (!mediaInfoRendered[year]) {
            mediaInfoRendered[year] = {
              data: [], 
              year: year,
              activeIndex: ref(0),
              renderedOverviewTextWidth: 0, 
              renderedOverviewContainerWidth: 0
            }
          }
          mediaInfoRendered[year].data.push(info)
        }
        console.log("=====================mediaInfoRendered...")
        console.log(mediaInfoRendered)
        console.log("=====================mediaInfoRendered end")
      } else {
        // uniqye years가 8개 이상일 때, year를 10년 단위로 그룹을 묶어서 카테고리 개수 줄이기를 시도한다.

      } // if (uniqueYears.size <= 9999) end
    } // if (length > 0) end
  })
*/ // deprecated

  const stripContent = (string) => {
    return string.replace(/\n+/g, ' ');
  }

  const onWindowResize = async (event) => {
    // 아직 marquee-content, marquee-container이 새롭게 렌더링되기 전이기 때문에 기다린다.
    await nextTick()
    let mediaInfoRendered = mediaInfoRenderedRef.value
    for (let yearCategory of Object.keys(mediaInfoRendered)) {
      setRenderedOverviewTextWidth(yearCategory)
      setRenderedOverviewContainerWidth(yearCategory)
      const translateEnd = getTranslateEnd(yearCategory)
      getMarqueeContentElement(yearCategory).style.setProperty('--translate-end', translateEnd + 'px')
      getMarqueeContentElement(yearCategory).style.animationPlayState = 'stop'
      getMarqueeContentElement(yearCategory).style.animation = 'none';
      setTimeout(() => {
        const duration = calculateAnimDuration(-translateEnd)
        getMarqueeContentElement(yearCategory).style.animation = `marquee ${duration}s linear infinite`;
        getMarqueeContentElement(yearCategory).style.animationDelay = '0s'; // 처음 시간으로 이동
        getMarqueeContentElement(yearCategory).style.animationPlayState = 'running'; // 애니메이션 재개
      }, 0);
    }
  }

  const onSwiperslidechange = async (event) => {
    console.log('slide changed')
    console.log(event.target) // swiper-container
    const yearCategory = event.target.getAttribute('attr-year')
    let mediaInfoRendered = mediaInfoRenderedRef.value
    mediaInfoRendered[yearCategory].activeIndex = event.target.swiper.activeIndex
    // 아직 marquee-content, marquee-container이 새롭게 렌더링되기 전이기 때문에 기다린다.
    await nextTick();
    setRenderedOverviewTextWidth(yearCategory)
    setRenderedOverviewContainerWidth(yearCategory)
    const translateEnd = getTranslateEnd(yearCategory) // getRendered...에 제대로 된 값들이 들어가야 getTranslateEnd()도 올바르게 계산된다.
    getMarqueeContentElement(yearCategory).style.setProperty('--translate-end', translateEnd + 'px')
    getMarqueeContentElement(yearCategory).style.animationPlayState = 'stop'
    getMarqueeContentElement(yearCategory).style.animation = 'none';
    setTimeout(() => {
      const duration = calculateAnimDuration(-translateEnd)
      console.log(mediaInfoRendered[yearCategory]['data'][mediaInfoRendered[yearCategory].activeIndex]['overview'])
      console.log("duration: " + duration)
      getMarqueeContentElement(yearCategory).style.animation = `marquee ${duration}s linear infinite`;
      getMarqueeContentElement(yearCategory).style.animationDelay = '0s'; // 처음 시간으로 이동
      getMarqueeContentElement(yearCategory).style.animationPlayState = 'running'; // 애니메이션 재개
    }, 0);
    console.log('slide changed end')
  }

  const calculateAnimDuration = (distance) => {
    return Math.max(5, Math.floor(distance / SCROLL_SPEED))
  }

  const getMarqueeContentElement = (yearCategory) => {
    return Array.from(document.querySelectorAll(".marquee-content")).find((e) => e.getAttribute("attr-year") == yearCategory)
  }
  const getMarqueeContainerElement = (yearCategory) => {
    return Array.from(document.querySelectorAll(".marquee-container")).find((e) => e.getAttribute("attr-year") == yearCategory)
  }
  const setRenderedOverviewTextWidth = (yearCategory) => {
    let mediaInfoRendered = mediaInfoRenderedRef.value
    mediaInfoRendered[yearCategory].renderedOverviewTextWidth = getMarqueeContentElement(yearCategory).offsetWidth
  }
  const getRednderedOverviewTextWidth = (yearCategory) => {
    let mediaInfoRendered = mediaInfoRenderedRef.value
    return mediaInfoRendered[yearCategory].renderedOverviewTextWidth
  }
  const setRenderedOverviewContainerWidth = (yearCategory) => {
    let mediaInfoRendered = mediaInfoRenderedRef.value
    mediaInfoRendered[yearCategory].renderedOverviewContainerWidth = getMarqueeContainerElement(yearCategory).offsetWidth
  }
  const getRenderedOverviewContainerWidth = (yearCategory) => {
    let mediaInfoRendered = mediaInfoRenderedRef.value
    return mediaInfoRendered[yearCategory].renderedOverviewContainerWidth
  }
  const getTranslateEnd = (yearCategory) => {
    // console.log("===============getTranslateEnd...")
    // console.log(`yearCategory : ${yearCategory}`)
    // console.log(getRednderedOverviewTextWidth(yearCategory))
    // console.log(getRenderedOverviewContainerWidth(yearCategory))
    // console.log("===============getTranslateEnd...")
    let px = -(getRednderedOverviewTextWidth(yearCategory) - getRenderedOverviewContainerWidth(yearCategory)) - 24 // padding : 12px
    return px >= 0 ? 0 : px
  }

  const posterPath = (string) => {
    // console.log("posterPath()...")
    // console.log("===string : ")
    // console.log(string)
    // console.log("posterPath() end")
    if (string) {
      return posterBaseUrl + string 
    } else {
      return noImageUrl
    }
  }

  const backDropPath = (string) => {
    if (string) {
      return posterBaseUrl + string 
    } else {
      return noImageUrl
    }
  }

  // TODO : id도 굳이 pinia에 저장할 필요 있나?
  // TODO : "movie"인지, "tv"인지를 저장해야 함(그것에 따라 요청해야 하는 외부 api가 다름)
  // deprecated
  // import { useMediaDetailStore } from '../../composables/stores/MediaDetail'
  // const store = useMediaDetailStore()
  // const handleClick = (mediaId, backDropPath) => {
  //   store.setMediaDetail({
  //     id: mediaId,
  //     backDropPath: backDropPath
  //   })
  // }
</script>