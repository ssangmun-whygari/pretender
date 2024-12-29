<template>
  <h1 v-show="mediaInfo.length > 0">tv 쇼</h1> <!--v-if 어째선지 안됨...-->
  <h2 v-if="showByIndividualYears == true">개별 연도로 표시</h2>
  <v-container>
    <div v-if="showByIndividualYears == true">
      <div v-for="(item, _) in Object.values(mediaInfoRendered).sort((a, b) => b.year - a.year)">
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
            v-for="(innerItem, index) in mediaInfoRendered[item.year]['data']"
          >
            <v-sheet class="slide py-3 w-100 h-100 border rounded"
              :elevation="5">
              <RouterLink 
              class="h-100 w-100 d-flex justify-center"
              :to="{path: '/detail', query: {id : innerItem['id']}}"
              v-on:click.prevent="handleClick(innerItem['id'], backDropPath(innerItem['backdrop_path']))">
              <img 
                :src="posterPath(innerItem['poster_path'])"
                class="poster"
              ></img>
              </RouterLink>
            </v-sheet>
          </swiper-slide>
        </swiper-container>
        <v-sheet class="d-flex justify-center slide mb-3 py-3 w-100 border rounded"
          :elevation="5"
        >
           <h2>{{ item['data'][item.activeIndex.value].name }}</h2>
        </v-sheet>
        <v-sheet class="marquee-container pa-3 border" :attr-year="item.year">
          <div class="marquee-content" :attr-year="item.year">
            {{ stripContent(item['data'][item.activeIndex.value].overview) }}
          </div>
        </v-sheet>
      </div>
    </div>
  </v-container>
</template>

<style>
  a:link {
    color:black;
    text-decoration:none;
  }
  a:visited {
    color:black;
    text-decoration:none;
  }
  .yearCategory {
    color: rgb(170, 170, 170);
    font-size: 250%;
    font-style: italic;
  }

  .mySwiper {
    width: 100%;
    height: 300px;
    /* background-color: rgb(250, 239, 175); */
  }

  .slide {
    box-sizing: border-box;
    /* border: 1px solid;
    border-radius: 5%;
    background-color: rgb(185, 185, 255);
    width: 200px; */
  }

  .poster {
    width: 70%;
    object-fit: contain;
  }

  .marquee-container {
    overflow: hidden;
    white-space: nowrap;
    position: relative;
    height: 50px;
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
  import { ref, onMounted, watch, nextTick } from 'vue'
  register();
  const YEAR_CATEGORY_NUMBER = 7
  const SCROLL_SPEED = 100
  const itemsPerRow = 3
  const posterBaseUrl = "http://image.tmdb.org/t/p/w500/"
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
  const props = defineProps({
      mediaInfo: Object
    })

  const mediaInfo = computed(() => {
    console.log("computed.....")
    console.log(props.mediaInfo)
    console.log(props.mediaInfo.tv)
    console.log("computed..... end")
    return props.mediaInfo.tv
  })
  let showByIndividualYears = ref(false)
  let mediaInfoRendered = {}

  watch(() => {return mediaInfo.value.length}, (length) => {
    // mediaInfo에 값이 채워지면 mediaInfoRendered의 값을 만든다.
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
      if (uniqueYears.size <= 9999) {
        console.log("showByIndividualYears : true")
        showByIndividualYears.value = true
        console.log("=====================mediaInfo.value...")
        console.log(mediaInfo.value)
        console.log("=====================mediaInfo.value end")
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
        console.log(mediaInfoRendered)
      }

      // DOM이 렌더링될떄까지 기다린 다음 swiperslidechange에 이벤트 리스너를 붙인다.
      nextTick(() => {
        const swipers = document.querySelectorAll('swiper-container')
        // reference : https://inpa.tistory.com/entry/JS-%F0%9F%93%9A-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EC%A0%9C%EA%B1%B0-%ED%95%9C%EB%B2%88%EB%A7%8C-%EC%8B%A4%ED%96%89%EB%90%98%EA%B2%8C-%ED%95%98%EA%B8%B0-removeEventListener-once
        console.log("swipers : ")
        console.log(swipers)
        console.log("swipers end")

        for (let swiper of swipers) {
          const yearCategory = swiper.getAttribute('attr-year')
          setRenderedOverviewContainerWidth(yearCategory)
          setRenderedOverviewTextWidth(yearCategory)
          // NOTE : <div ... :style="{'--translate-end': getTranslateEnd(item.year) + 'px'}">로 하면  getTranslateEnd()가 setRenderedOverview...보다 빨리 계산되서 안됨
          // 어쩔 수 없이 자바스크립트로 처리함
          const translateEnd = getTranslateEnd(yearCategory)
          const duration = calculateAnimDuration(-translateEnd)
          getMarqueeContentElement(yearCategory).style.setProperty('--translate-end', translateEnd + 'px')
          getMarqueeContentElement(yearCategory).style.animation = `marquee ${duration}s linear infinite`;
          getMarqueeContentElement(yearCategory).style.animationDelay = '0s'; // 처음 시간으로 이동
          getMarqueeContentElement(yearCategory).style.animationPlayState = 'running'; // 애니메이션 시작
          swiper.removeEventListener('swiperslidechange', onSwiperslidechange)
          swiper.addEventListener('swiperslidechange', onSwiperslidechange)
        }

        window.addEventListener("resize", onWindowResize)
      })
    }
  })

  const stripContent = (string) => {
    return string.replace(/\n+/g, ' ');
  }

  const onWindowResize = async (event) => {
    // 아직 marquee-content, marquee-container이 새롭게 렌더링되기 전이기 때문에 기다린다.
    await nextTick()
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
    mediaInfoRendered[yearCategory].activeIndex.value = event.target.swiper.activeIndex
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
      console.log(mediaInfoRendered[yearCategory]['data'][mediaInfoRendered[yearCategory].activeIndex.value]['overview'])
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
    mediaInfoRendered[yearCategory].renderedOverviewTextWidth = getMarqueeContentElement(yearCategory).offsetWidth
  }
  const getRednderedOverviewTextWidth = (yearCategory) => {
    return mediaInfoRendered[yearCategory].renderedOverviewTextWidth
  }
  const setRenderedOverviewContainerWidth = (yearCategory) => {
    mediaInfoRendered[yearCategory].renderedOverviewContainerWidth = getMarqueeContainerElement(yearCategory).offsetWidth
  }
  const getRenderedOverviewContainerWidth = (yearCategory) => {
    return mediaInfoRendered[yearCategory].renderedOverviewContainerWidth
  }
  const getTranslateEnd = (yearCategory) => {
    console.log("===============getTranslateEnd...")
    console.log(`yearCategory : ${yearCategory}`)
    console.log(getRednderedOverviewTextWidth(yearCategory))
    console.log(getRenderedOverviewContainerWidth(yearCategory))
    console.log("===============getTranslateEnd...")
    let px = -(getRednderedOverviewTextWidth(yearCategory) - getRenderedOverviewContainerWidth(yearCategory)) - 24 // padding : 12px
    return px >= 0 ? 0 : px
  }

  const posterPath = (string) => {
    if (string) {
      return posterBaseUrl + string 
    } else {
      noImageUrl
    }
  }

  const backDropPath = (string) => {
    if (string) {
      return posterBaseUrl + string 
    } else {
      noImageUrl
    }
  }

  // TODO : id도 굳이 pinia에 저장할 필요 있나?
  // TODO : "movie"인지, "tv"인지를 저장해야 함(그것에 따라 요청해야 하는 외부 api가 다름)
  import { useMediaDetailStore } from '../stores/MediaDetail'
  const store = useMediaDetailStore()
  const handleClick = (mediaId, backDropPath) => {
    store.setMediaDetail({
      id: mediaId,
      backDropPath: backDropPath
    })
  }
</script>