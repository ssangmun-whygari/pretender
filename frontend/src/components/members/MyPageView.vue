<template>
  <AppHeader></AppHeader>
  <h1>마이페이지</h1>
  <h2>환영합니다. {{ userId }} 님</h2>
  <v-container fluid>
    <v-row justify="center">
      <v-col lg="8" cols="12">
        <div class="d-flex align-center">
          <div class="d-flex align-center" v-if="watchList.loaded == true && watchList.data.length > 0">
            <h1 class="mr-3">내가 본 작품 리스트</h1>
            <v-btn class="mr-3" color="primary">펼쳐보기</v-btn>
            <v-icon icon="mdi-cog" size="large" @click="toggleShowOptions('collection-primary')"/>
          </div>
          <div v-else>
            <h1>내가 본 작품 리스트</h1>
          </div>
        </div>
        <div v-if="watchList.loaded == true && watchList.data.length > 0" class="position-relative">
          <swiper-container 
            class="slider"
            space-between="30"
            slides-per-view="5"
          >
            <swiper-slide 
              class="slide"
              v-for="(item, index) in watchList.data"
            >
              <v-sheet class="ma-3 py-3 w-100 h-100" :elevation="10">
                <RouterLink
                  class="w-100 h-100 d-flex flex-column justify-center align-center"
                  :to="{path: '/detail', query: {id : item.mediaId}}"
                  v-on:click.prevent="handleClick(item.mediaId, backDropPath(item.backDropPath))">
                  <img
                    :src="posterPath(item.posterPath)"
                    class="poster mb-1"
                  ></img>
                  <div>{{ shorten(item.mediaTitle) }}</div>
                </RouterLink>
              </v-sheet>
            </swiper-slide>
          </swiper-container>
          <div class="backdrop" id="collection-primary">
            <div class="position-relative d-flex justify-center align-center w-100 h-100">
              <div>
                <v-checkbox style="color: white;" color="primary" density="compact" label="리스트 공개하기"
                  v-model="optionStates['collection-primary'].options.publicList"></v-checkbox>
                <v-checkbox color="primary" density="compact" label="댓글 허용"
                  v-model="optionStates['collection-primary'].options.allowReply"></v-checkbox>
              </div>
              <div class="position-absolute ma-3 top-0 right-0">
                <v-icon icon="mdi-window-close" size="large" @click="closeShowOptions('collection-primary')"/>
              </div>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<style>
  /* reference : https://codepen.io/tony19/pen/PoWMroG */
  .v-input__control .v-label {
    opacity: 100%;
  }

  .backdrop {
    position: absolute;
    top: 0%;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.7);
    z-index: 1;
    display: none;
    color: white;
  }

  .slider {
    /* border: 1px solid; */
    /* background-color: rgb(185, 185, 255); */
  }

  .slide {
    box-sizing: border-box;
    /* width: 350px; */
    /* border: 1px solid; */
    /* border-radius: 5%;
    background-color: rgb(185, 185, 255);
    width: 200px; */
  }
</style>

<script setup>
import { register } from 'swiper/element/bundle'
register();

import AppHeader from '../AppHeader.vue';
import { reactive, ref, watch } from 'vue'
import axios from 'axios'

let userId = ref('')
let watchList = ref({data: [], loaded: false})
const posterBaseUrl = "http://image.tmdb.org/t/p/w500/"
const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
let optionStates = reactive({})

watch(() => {return watchList.value.loaded}, (bool) => {
  if (bool == true) {
    // NOTE : 반응형 객체에 값을 동적으로 추가하기 위해 ref()가 아니라 reactive()를 사용
    // NOTE : reactive에서는 optionsStates.value로 접근하지 않음
    optionStates["collection-primary"] = {
      "visible" : false,
      "options" : {
        "publicList" : true,
        "allowReply" : false,
      }
    }
    console.log("watchList loaded...")
    console.log(optionStates)
    console.log("watchList loaded end")
    // TODO : 서버에 보내서 옵션 상태 갱신하기
    // TODO : 서버에서 갱신이 안되면 토스트로 에러 메시지 띄우기
    // allowReply 해제할 때 답글이 있으면 '지금까지 달린 답글은 비공개처리됩니다'라고 토스트 띄우기
  }
})

async function getUser() {
  let response = await axios.get(
    'http://localhost:8080/api/authenticated',
    {
      withCredentials: true,

      headers: {
        "X-Requested-With": "XMLHttpRequest"
      }
    }
  ) // axios.get end
  if (!response.data.principal) { // 로그인 안되어 있음
    return 
  }
  userId.value = response.data.principal.username
}
getUser()

async function getWatchList() {
  let response = await axios.get(
    'http://localhost:8080/api/collection/watchList',
    {
      withCredentials: true,

      headers: {
        "X-Requested-With": "XMLHttpRequest"
      }
    }
  ) // axios.get end
  console.log("response : ")
  console.log( response)
  watchList.value.data = response.data
  watchList.value.loaded = true
}
getWatchList()

const backDropPath = (string) => {
  if (string) {
    return posterBaseUrl + string 
  } else {
    noImageUrl
  }
}

const posterPath = (string) => {
  if (string) {
    return posterBaseUrl + string 
  } else {
    noImageUrl
  }
}

const shorten = (string) => {
  if (string.length > 12) {
    string = string.substr(0, 12)
    string += "..."
  }
  return string
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

const toggleShowOptions = (elementId) => {
  console.log("toggleShowOptions...")
  let element = document.getElementById(elementId)
  let computedStyle = window.getComputedStyle(element).display;
  if (computedStyle === "none") {
    element.style.display = "flex";
  } else {
    element.style.display = "none";
  }
}

const closeShowOptions = (elementId) => {
  let element = document.getElementById(elementId)
  element.style.display = "none"
}
</script>