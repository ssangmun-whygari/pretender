<template>
  <div class="bg-container"></div>
  <v-container class="d-flex justify-end position-relative">
    <div v-if="isAuthenticated == true">
      <RouterLink to="/myPage">
        마이페이지
      </RouterLink> / 
      <RouterLink to="logout" @click="onLogout">
        로그아웃
      </RouterLink>
    </div>
    <div v-else>
      <RouterLink to="/signUp" @click="savePreviousPage">
        회원가입
      </RouterLink> / 
      <RouterLink to="/login">
        로그인
      </RouterLink>
    </div>
  </v-container>
  <v-container class="d-flex flex-column justify-center position-relative" style="height:100%">
    <v-row justify="center">
      <v-col cols="12">
        <v-row justify="center" align="end">
          <v-col lg="6" cols="12" class="order-2 order-lg-1">
            <div class="logo-wrapper">
              <div class="logo">pretender</div>
              <div class="logo-animated">pretender</div>
            </div>
          </v-col>
          <v-col lg="6" cols="12" class="order-1 order-lg-2">
            <div class="main-poster-container">
              <RouterLink 
                class="d-flex justify-center h-100 w-100"
                :to="{path: '/detail', query: {id : mainPosterTMDBid, type : 'movie'}}"
              >
              </RouterLink>
              <div class="overlay-text">{{ mainPosterName ? mainPosterName : "제목 없음" }}</div>
            </div>
          </v-col>
        </v-row>
        
        <div class="mt-5">
          <v-text-field
            class="mx-auto main-search-bar"
            bg-color="white"
            :max-width="lgAndUp ? 1000 : null"
            placeholder="영화, 애니메이션, TV 프로그램의 제목을 검색하세요"
            v-on:keyup.enter="onEnter"
            v-model="word"
          ></v-text-field>
        </div>
      </v-col>
      <v-col lg="9" cols="12">
        <v-sheet border class="mt-3 mb-3 pa-3 rounded-lg">
          <div style="font-size: xx-large; font-weight: bold;">🧐 똑똑한 AI 요약이 제공되는 작품 리스트에요</div>
        </v-sheet>
        <v-sheet border class="mt-3 mb-3 pa-3 rounded-lg" v-if="aiSummaryProvidingList.length > 0">
          <div v-if="aiSummaryWorksMediaInfoList.length > 0">
            <swiper-container
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
                v-for="(innerItem, index) in aiSummaryWorksMediaInfoList"
                >
                <v-sheet class="ma-3 pa-3 w-100 h-100 border rounded-lg poster-image-sheet"
                  :elevation="2">
                  <RouterLink 
                  class="d-flex justify-center h-100 w-100"
                  :to="{path: '/detail', query: {id : innerItem['mediaId'], type : 'tv'}}" >
                    <div class="poster-image-container">
                      <img 
                      :src="posterPath(innerItem['posterPath'])"
                      class="poster"
                      @load="onPosterImageLoad"
                      ></img>
                    </div>
                  </RouterLink>
                </v-sheet>
                <v-sheet class="ma-3 pa-3 w-100 h-100 border rounded-lg">
                  <div style="text-align: center"><b>{{ innerItem["title"] }}</b></div>
                </v-sheet>
              </swiper-slide>
            </swiper-container>
          </div>
        </v-sheet>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
  .logo-wrapper {
    position: relative;
    text-align: center;
  }

  .logo {
    font-size: 100px;
    font-weight: bold;
    position: relative;
  }

  .logo-animated {
    font-size: 100px;
    font-weight: bold;
    position: absolute;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    animation: zoomFadeOut 1.5s ease-out forwards;
    animation-delay: 1.5s;
  }

  .main-poster-container::before,
  .main-poster-container::after {
    pointer-events: none;
  }

  .main-poster-container {
    position: relative;
    height: 600px;
    border-radius: 20px;
    overflow: hidden;
  }

  .main-poster-container::after {
    content: "";
    position: absolute;
    top: 0; left: 0;
    width: 100%; height: 100%;
    background-image: var(--main-poster-url);
    background-size: cover;
    background-position: center;
    transition: transform 0.5s ease;
    z-index: 0;
  }

  .main-poster-container:hover::after {
    transform: scale(1.1);
  }

  .main-poster-container .overlay-text {
    position: absolute;
    top: 100%;
    left: 1%;
    transform: translate(0%, -100%);
    color: white;
    font-size: 50px;
    opacity: 0;
  }

  .main-poster-container:hover .overlay-text {
    z-index: 2;
    opacity: 1;
  }

  .main-poster-container::before {
    content: "";
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    border-radius: 20px;
    opacity: 0;
    z-index: 1;
  }

  .main-poster-container:hover::before {
    opacity: 1;
  }

  @keyframes zoomFadeOut {
    0% {
      transform: translateX(-50%) scale(1);
      opacity: 1;
    }
    100% {
      transform: translateX(-50%) scale(2);
      opacity: 0;
    }
  }

  .bg-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-size: cover;
    background-position: top;
    background-attachment: fixed;
    background-image: var(--background-image-url);
    overflow: hidden;
  }

  a:link {
    color:black;
    text-decoration:none;
  }
  a:visited {
    color:black;
    text-decoration:none;
  }
</style>

<script setup>
  import { useRouter, useRoute } from 'vue-router';
  import { useDisplay } from 'vuetify';
  import axios from 'axios'
  import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
  import { useNavigationStore } from '@/composables/stores/navigation';
  import { usePageTransition } from '@/composables/pageTransition';
  import PopularMovieCarousel from './PopularMovieCarousel.vue';
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const backgroundImageUrl = apiBaseUrl + '/resource/backgroundImage'
  const posterBaseUrl = "http://image.tmdb.org/t/p/w780"
  const loading = ref(true)
  const mainPosterPath = ref('')
  const mainPosterName = ref('')
  const mainPosterTMDBid = ref(0)
  const aiSummaryProvidingList = ref([])
  const aiSummaryWorksMediaInfoList = ref([])
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
  const posterPath = (string) => {
    if (string) {
      return posterBaseUrl + string 
    } else {
      return noImageUrl
    }
  }
  import { register } from 'swiper/element/bundle'
  register()

  onMounted(() => {
    document.querySelector('.bg-container').style.setProperty('--background-image-url', `url(${backgroundImageUrl})`)
  })

  let popularMovieInfos = ref([])
  let requestPopularMovies = async () => {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
    let response = await axios.get(
      apiBaseUrl + '/api/popularMovies',
    )
    popularMovieInfos.value = popularMovieInfos.value.concat(response.data.results)
  }
  requestPopularMovies()
  watch(() => {return popularMovieInfos.value.length}, (length) => {
    if (length > 0) { // popularMovieInfos에 값이 채워지면
      let movieInfoObj = popularMovieInfos.value.find((e) => { return e.poster_path })
      let mainPosterPath = posterBaseUrl + movieInfoObj.poster_path
      loading.value = false
      mainPosterName.value = movieInfoObj.title
      mainPosterTMDBid.value = movieInfoObj.id
      nextTick(() => {
        document.querySelector('.main-poster-container').style.setProperty('--main-poster-url', `url(${mainPosterPath})`)
      })
    }
  })

  // AI 요약이 첨부된 작품 리스트 받기
  let requestAiSummaryProvidingList = async () => {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
    let response = await axios.get(
      apiBaseUrl + '/api/aiSummaryProvided',
    )
    console.log("==================requestAiSummaryProvidingList")
    console.log(response)
    let sourceList = response.data
    let list = sourceList.sort(() => Math.random() - 0.5).slice(0, 10)// 랜덤으로 10개 추출
    console.log(list)
    aiSummaryProvidingList.value = aiSummaryProvidingList.value.concat(list)
  }
  requestAiSummaryProvidingList()
  let requestMediaInfoForAiSummaryProvidingList = async (idList) => {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
    let response = await axios.post(
      apiBaseUrl + '/api/detail/aiSummaryProvided', aiSummaryProvidingList.value
    )
    console.log("========================requestMediaInfoForAiSummaryProvidingList")
    console.log(response.data)
    aiSummaryWorksMediaInfoList.value = aiSummaryWorksMediaInfoList.value.concat(response.data)
  }

  watch(() => {return aiSummaryProvidingList.value.length}, (length) => {
      if (length > 0) {
        requestMediaInfoForAiSummaryProvidingList()
      }
    } 
  )

  const { lgAndUp } = useDisplay();
  const router = useRouter();
  const route = useRoute();
  const navigationStore = useNavigationStore();
  const pageTransition = usePageTransition();

  let word = ref('')
  let responseData = ref(null)
  let isAuthenticated = ref(false)

  async function onEnter() {
    pageTransition.trigger({
      path: '/search',
      query: {
        word: word.value
      }
    })
  }

  async function checkAuthenticated() {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
    let response = await axios.get(
      apiBaseUrl + '/api/authenticated',
      {
        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ) // axios.get end
    return response.data && response.data.authenticated == true
  }

  function savePreviousPage(){
    navigationStore.setPreviousPage(route.fullPath);
  }

  function onLogout() {
    savePreviousPage()
  }

  // note : v-if하고 같이 쓰려면 onMounted()로 써야 하는 것 같음
  onMounted(async () => {
    isAuthenticated.value = await checkAuthenticated()
  })
</script>