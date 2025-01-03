<template>
  <AppHeader></AppHeader>
  <h1>마이페이지</h1>
  <h2>환영합니다. {{ userId }} 님</h2>
  <v-container fluid>
    <v-row justify="center">
      <v-col lg="8" cols="12">
        <h1>내가 본 작품 리스트</h1>
        <div v-if="watchList.loaded == true && watchList.data.length > 0">
          <swiper-container 
          class="mySwiper"
          slides-per-view="4"
          space-between="30"
          >
            <swiper-slide 
              v-for="(item, index) in watchList.data"
            >
              <v-sheet class="py-1 w-100 h-100" :elevation="1">
                <RouterLink 
                  class="w-100 h-100 d-flex justify-center"
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
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { register } from 'swiper/element/bundle'
register();

import AppHeader from '../AppHeader.vue';
import { ref, watch } from 'vue'
import axios from 'axios'

let userId = ref('')
let watchList = {data: [], loaded: false}

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
  watchList.data = response.data
  watchList.loaded = true
}
getWatchList()


</script>