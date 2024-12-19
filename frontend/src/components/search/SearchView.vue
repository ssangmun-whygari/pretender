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
  let mediaInfo = ref([]) // 자식 컴포넌트가 length로 접근하기 때문에 에러 방지

  // axios 요청
  async function getResponse() {
    let response = await axios.get(
      'http://localhost:8080/search',
      {
        params : {
          query: word
        }
      }
    ) // axios.get end
    console.log("==============================")
    console.log(response)
    console.log(response.data)
    console.log("==============================")
    mediaInfo.value = mediaInfo.value.concat(response.data.results)
  }
  getResponse()
</script>