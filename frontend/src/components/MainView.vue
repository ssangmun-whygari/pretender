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
    <div v-else class="d-flex">
      <SignUpModal></SignUpModal> / 
      <LoginModal @requestCheckAuthenticated="executeCheckAuthenticated"></LoginModal>
    </div>
  </v-container>
  
  <v-container class="ma-0 pa-0 position-relative" style="max-width: none;">
    <PopularMovieCarouselV2 :imagePaths="imagePathsForPopularMovieCarousel"></PopularMovieCarouselV2>
    <div class="logo">
      pretender
    </div>
    <div class="main-search-bar">
      <v-text-field
        class="mx-auto"
        bg-color="white"
        :max-width="lgAndUp ? 1000 : null"
        placeholder="영화, 애니메이션, TV 프로그램의 제목을 검색하세요"
        v-on:keyup.enter="onEnter"
        v-model="word"
      ></v-text-field>
    </div>
  </v-container>
  <v-container class="w-100 d-flex flex-column justify-center position-relative">
    <v-row justify="center">

      <v-col class="mt-5" cols="12">
        <v-row justify="center">
          <v-col lg="5" cols="6" class="emphasized-bg-left d-flex flex-column justify-center ga-3" style="background-color: #00A1FF;">
            <div class="ml-3" style="font-size: 48px;">사람처럼 똑똑합니다.</div>
            <div class="ml-3" style="font-size: 20px;">- 똑똑한 AI 요약 정보로 '아는 척' 해보세요.</div>
          </v-col>
          <v-col lg="4" cols="6">
            <TresCanvas>
              <TresPerspectiveCamera
                :position="[0, 4, 7.2]"
                :look-at="[0, 1, 0]"
                :fov="30"
              />
              <Suspense>
                <PopCorn/>
              </Suspense>
              <TresDirectionalLight
                :position="[5, 0, 5]"
                :intensity="1"
              ></TresDirectionalLight>
              <TresDirectionalLight
                :position="[-5, 0, 5]"
                :intensity="0.5"
                :color="0xFF8400"
              ></TresDirectionalLight>
              <TresAmbientLight :intensity="1.5" />
            </TresCanvas>
          </v-col>
        </v-row>
      </v-col>

      <v-col class="mt-5" lg="9" cols="12">
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

      <v-col class="mt-5" cols="12">
        <v-row justify="center">
          <v-col lg="4" cols="6">
            <TresCanvas>
              <Suspense>
                <MovieCut/>
              </Suspense>
              <TresPerspectiveCamera
                :position="[4, -2.4, 1.6]"
                :look-at="[0, 0, 0]"
              />
              <TresSpotLight
                :position="[2, 2, 2]"
                :intensity="15"
              ></TresSpotLight>
            </TresCanvas>
          </v-col>
          <v-col lg="5" cols="6" class="emphasized-bg-right d-flex flex-column justify-center align-end ga-3" style="background-color: #00CEB6;">
            <div class="mr-3" style="font-size: 48px;">당신의 최애는 누구인가요?</div>
            <div class="mr-3" style="font-size: 20px;">- 최애에게 소중한 한표를 주세요!</div>
          </v-col>
        </v-row>
      </v-col>

    </v-row>
  </v-container>
</template>

<style scoped>
  .main-search-bar {
    width: 100%;
    margin-top: 50px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 3;
  }

  .logo {
    position: absolute;
    top: 40%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 100px;
    font-weight: bold;
    color: white;
    z-index: 3;
  }

  .emphasized-bg-left {
    border-radius: 0px 100px 100px 0px / 0px 200px 200px 0px;
    height: 400px;
    color: white;
  }

  .emphasized-bg-right {
    border-radius: 100px 0px 0px 100px / 200px 0px 0px 200px;
    height: 400px;
    color: white;
  }

  .main-bg-poster-container {
    width: 100%;
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
  // 3d model 컴포넌트 import
  import PopCorn from './3Dmodel/PopCorn.vue';
  import MovieCut from './3Dmodel/MovieCut.vue';
  import axios from 'axios'
  import { TresCanvas } from '@tresjs/core';
  import { useRouter, useRoute } from 'vue-router';
  import { useDisplay } from 'vuetify';
  import { ref, onMounted, onUnmounted, nextTick, watch, onBeforeUnmount } from 'vue'
  import { useNavigationStore } from '@/composables/stores/navigation';
  import { usePopularMoviesStore } from '@/composables/stores/popularMovies';
  import { usePageTransition } from '@/composables/pageTransition';
  import { useCheckAuthenticated } from '@/composables/checkAuthenticated';
  
  const { lgAndUp } = useDisplay();
  const router = useRouter();
  const route = useRoute();
  const navigationStore = useNavigationStore();
  const pageTransition = usePageTransition();
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
  let popularMoviesStore = usePopularMoviesStore()
  let requestPopularMovies = async () => {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
    let response = await axios.get(
      apiBaseUrl + '/api/popularMovies',
    )
    popularMoviesStore.setPopularMovieInfos(response.data.results)
  }
  let imagePathsForPopularMovieCarousel = ref([])
  let requestAiSummaryProvidingList = async () => {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
    let response = await axios.get(
      apiBaseUrl + '/api/aiSummaryProvided',
    )
    let sourceList = response.data
    let list = sourceList.sort(() => Math.random() - 0.5).slice(0, 10)// 랜덤으로 10개 추출
    aiSummaryProvidingList.value = aiSummaryProvidingList.value.concat(list)
  }
  let requestMediaInfoForAiSummaryProvidingList = async (idList) => {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
    let response = await axios.post(
      apiBaseUrl + '/api/detail/aiSummaryProvided', aiSummaryProvidingList.value
    )
    aiSummaryWorksMediaInfoList.value = aiSummaryWorksMediaInfoList.value.concat(response.data)
  }
  
  onMounted(() => {
    document.querySelector('.bg-container').style.setProperty('--background-image-url', `url('/images/SL-120722-54440-04.jpg')`)
  })

  if (popularMoviesStore.getPopularMovieInfos().value.length === 0) {
    requestPopularMovies()
  }
  watch(() => {return popularMoviesStore.getPopularMovieInfos().value.length}, (length) => {
      if (length > 0) {
        let infos = popularMoviesStore.getPopularMovieInfos().value
        imagePathsForPopularMovieCarousel.value = 
        imagePathsForPopularMovieCarousel.value.concat(
          infos.slice(0, 5).filter((entry) => entry["backdrop_path"] !== undefined).map((entry) => entry["backdrop_path"])
        )
      }
    } 
  )
  onBeforeUnmount(() => { 
    popularMoviesStore.setPopularMovieInfos([]) // store에 저장된 value는 이 컴포넌트가 언마운트가 되어도 초기화가 안되기때문에 초기화해줌
  })

  // AI 요약이 첨부된 작품 리스트 받기
  requestAiSummaryProvidingList()
  watch(() => {return aiSummaryProvidingList.value.length}, (length) => {
      if (length > 0) {
        requestMediaInfoForAiSummaryProvidingList()
      }
    } 
  )

  
  let word = ref('')
  let isAuthenticated = ref(false)
  async function onEnter() {
    pageTransition.trigger({
      path: '/search',
      query: {
        word: word.value
      }
    })
  }
  function onLogout() {
    isAuthenticated.value = false
    savePreviousPage()
  }
  function savePreviousPage(){
    navigationStore.setPreviousPage(route.fullPath);
  }
  // 자식 컴포넌트가 보낸 이벤트(요청)을 처리
  async function executeCheckAuthenticated() {
    // console.log("자식이 보낸 이벤트 받음")
    isAuthenticated.value = await useCheckAuthenticated();
  }
  // note : v-if하고 같이 쓰려면 onMounted()로 써야 하는 것 같음
  onMounted(async () => {
    isAuthenticated.value = await useCheckAuthenticated()
  })
</script>