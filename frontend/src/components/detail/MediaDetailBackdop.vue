<template>
  <v-sheet class="position-relative">
    <div>
      <v-img
        :src="backDropFullPath(mediaInfo.backdrop_path)"
        height="600px"
        class="mx-auto"
        cover
      ></v-img>
    </div>
    <div class="position-absolute bottom-0 w-100 d-flex justify-space-between align-center pt-5" id="backdropInfo">
      <v-row>
        <v-col lg="6" cols="12" class="pa-0 pl-3 pb-3"><h1 class="ml-3 text-white">{{ title }}</h1></v-col>
        <v-col lg="6" cols="12" class="pa-0 pr-3 pb-3">
          <div class="mb-3 mr-3 d-flex align-center justify-end">
            <p class="text-white">평균별점 : </p>
            <v-rating :model-value="cutTo05Unit(averageStars)" :disabled="true"
              active-color="amber-accent-4" color="amber-accent-4" density="compact"size="x-large" half-increments/>
            <h1 class="text-white">{{ averageStars }}</h1>
            <v-btn v-if="hasWatched == false" @click="addToWatchList" class="ml-3" color="primary">내가 본 작품인가요?</v-btn>
            <v-btn v-else class="ml-3" color="secondary">내가 본 작품이에요!</v-btn>
          </div>
        </v-col>
      </v-row>
    </div>
  </v-sheet>
</template>

<style>
  #backdropInfo {
    background: linear-gradient(to top, rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5) 90%, rgba(0, 0, 0, 0) 100%);
  }
</style>

<script setup>
  import axios from 'axios'
  import { useMediaDetailStore } from '@/composables/stores/MediaDetail'
  import { useNavigationStore } from '@/composables/stores/navigation'
  import { useCheckAuthenticated } from '@/composables/checkAuthenticated';
  import { storeToRefs } from 'pinia';
  import { ref, toRef, computed } from 'vue'
  import { useRoute, useRouter } from 'vue-router';
  
  const emit =  defineEmits(['requestOpenLoginModal'])
  const props = defineProps({
      mediaInfo: Object
  })
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const backDropPathBaseUrl = "http://image.tmdb.org/t/p/w1280"
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
  const backDropFullPath = (string) => {
    if (string) {
      return backDropPathBaseUrl + string
    } else {
      return noImageUrl
    }
  }
  const hasWatched = ref(false)
  const route = useRoute()
  const id = route.query.id
  const type = route.query.type
  const router = useRouter()
  const navigationStore = useNavigationStore()
  const mediaInfo = toRef(props, 'mediaInfo') // reference : https://stackoverflow.com/questions/69855836/props-gotten-directly-from-vue-3-setup-are-not-reactive
  const title = computed(() => {
    return mediaInfo.value.name ? mediaInfo.value.name : mediaInfo.value.title
  })
  const averageStars = computed(() => {
    if (mediaInfo.value.average_stars) {
      let averageStars = mediaInfo.value.average_stars
      return Math.round(averageStars * 10) / 10
    } else {
      return 0.0
    }
  })

  const cutTo05Unit = (floatNum) => {
    // 예 : 2.5 ~ 2.9면 2.5로, 3,0이면 3.0으로 계산됨
    return ((floatNum * 10) - (floatNum * 10 % 5)) / 10
  }
  async function addToWatchList() {
    const isLogined = await useCheckAuthenticated(); // 결과를 기다림
    if (!isLogined) {
      console.log("MediaDetailBackdrop에서 requestOpenLoginModal를 요청함");
      emit("requestOpenLoginModal"); // 로그인이 되어있지 않으면 부모 컴포넌트에게 이벤트 보내서 로그인 모달 띄우게 함
      return;
    }

    let response = await axios.post(
      apiBaseUrl + '/api/collection/watchList',
      null, // 본문
      {
        withCredentials: true,
        headers: {
          "X-Requested-With": "XMLHttpRequest"
        },
        params : {
          mediaType: type,
          mediaId: id,
          mediaTitle: title.value
        }
      }
    )
    if (response.status == 200) hasWatched.value = true
  }
  async function getHasWatched() {
    const isLogined = await useCheckAuthenticated(); // 결과를 기다림
    if (!isLogined) return; // 로그인이 되어있지 않으면 실행하지 않음
    let response = await axios.get(
      apiBaseUrl + '/api/collection/watchList',
      {
        withCredentials: true,
        headers: {
          "X-Requested-With": "XMLHttpRequest"
        },
        params : {
          mediaType: type,
          mediaId: id
        }
      }
    )
    hasWatched.value = response.data;
  }
  
  getHasWatched()
</script>