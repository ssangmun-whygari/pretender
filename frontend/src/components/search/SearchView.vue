<template>
    <AppHeader></AppHeader>
    <MediaList v-bind:mediaInfo="mediaInfo"/>
</template>

<script setup>
  console.log("rendered...")
  import axios from 'axios'
  import { ref } from 'vue'
  import { useRoute } from 'vue-router';
  import AppHeader from '../AppHeader.vue';
  let word = useRoute().query.word // 주소창의 url에서 query string을 얻어옴
  let mediaInfo = ref({"tv": [], "movie": []}) // 자식 컴포넌트가 length로 접근하기 때문에 에러 방지

  // axios 요청
  async function getResponse() {
    let response = await axios.get(
      'http://localhost:8080/api/search',
      {
        params : {
          query: word,
          type: "tv"
        }
      }
    ) // axios.get end
    mediaInfo.value.tv = mediaInfo.value.tv.concat(response.data.results)
    console.log("==========mediaInfo=============")
    console.log(mediaInfo.value)
    console.log("==========mediaInfo=============")
  }
  getResponse()
</script>