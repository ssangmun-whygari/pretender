<template>
  <v-sheet class="position-relative">
    <div>
      <v-img
        :src="backDropPath"
        height="600px"
        class="mx-auto"
        cover
      ></v-img>
    </div>
    <div class="position-absolute bottom-0 w-100 d-flex justify-space-between align-center">
      <h1 class="mb-3 ml-3 text-white">
        {{ title }}
      </h1>
      <div class="mb-3 mr-3 d-flex align-center">
        <p class="text-white">평균별점 : </p>
        <v-rating
        active-color="amber-accent-4"
        color="amber-accent-4"
        density="compact"
        size="x-large"
        model-value="3.5"
        disabled="true"
        half-increments
        />
        <h1 class="text-white">3.5</h1>
        <v-btn v-if="hasWatched == false" class="ml-3" color="primary" @click="addToWatchList">내가 본 작품인가요?</v-btn>
        <v-btn v-else class="ml-3" color="secondary">내가 본 작품이에요!</v-btn>
      </div>
    </div>
  </v-sheet>
</template>

<script setup>
  import { useMediaDetailStore } from '../stores/MediaDetail'
  import { storeToRefs } from 'pinia';
  import { ref, toRef, computed } from 'vue'
  import axios from 'axios'
  import { useRoute } from 'vue-router';
  const props = defineProps({
      mediaInfo: Object
  })
  // pinia의 store에서 값 꺼내온다
  const store = useMediaDetailStore()
  const { backDropPath } = storeToRefs(store)
  const hasWatched = ref(false)
  const id = useRoute().query.id

  // reference : https://stackoverflow.com/questions/69855836/props-gotten-directly-from-vue-3-setup-are-not-reactive
  const mediaInfo = toRef(props, 'mediaInfo')
  const title = computed(() => {
    // console.log("==================title in computed...")
    // console.log(mediaInfo)
    // console.log(mediaInfo.value)
    // console.log(mediaInfo.name)
    // console.log(mediaInfo.value.name)
    // console.log("==================title in computed end")
    return mediaInfo.value.name ? mediaInfo.value.name : mediaInfo.value.title
  })
  

  async function getHasWatched() {
    let response = await axios.get(
      'http://localhost:8080/api/collection/watchList',
      {
        withCredentials: true,
        headers: {
          "X-Requested-With": "XMLHttpRequest"
        },
        // TODO : 바꿔야 함
        params : {
          mediaType: "tv",
          mediaId: id
        }
      }
    )
    console.log("getHasWatched()...")
    console.log(response)
    console.log("getHasWatched() end")
    hasWatched.value = response.data
  }
  getHasWatched()

  async function addToWatchList() {
    let response = await axios.post(
      'http://localhost:8080/api/collection/watchList',
      null, // 본문
      {
        withCredentials: true,
        headers: {
          "X-Requested-With": "XMLHttpRequest"
        },
        // TODO : 바꿔야 함
        params : {
          mediaType: "tv",
          mediaId: id,
          mediaTitle: title()
        }
      }
    )
    console.log("addToWatchList()...")
    console.log(response)
    console.log("addToWatchList() end")
    if (response.status == 200) {
      hasWatched.value = true
    }
  }
</script>