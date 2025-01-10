<template>
    <AppHeader></AppHeader>
    <!-- mediaListComponentKey가 바뀔떄마다 다시 컴포넌트가 렌더링됨 -->
    <MediaList :key="mediaListComponentKey" v-bind:mediaInfo="mediaInfo"/>
</template>

<script setup>
  console.log("rendered...")
  import axios from 'axios'
  import { ref, watch } from 'vue'
  import { useRoute } from 'vue-router';
  import AppHeader from '../AppHeader.vue';
  let route = useRoute()
  let mediaInfo = ref({"tv": [], "movie": []}) // 자식 컴포넌트가 length로 접근하기 때문에 에러 방지
  let mediaListComponentKey = 'initial'

  // axios 요청
  async function getResponse(word) {
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
    // console.log("==========mediaInfo=============")
    // console.log(mediaInfo.value)
    // console.log("==========mediaInfo=============")
  }
  getResponse(route.query.word)

  // 쿼리 변경 감지
  watch(
    () => { return route.query.word },
    (newWord) => {
      if (newWord) {
        mediaInfo.value = {"tv": [], "movie": []}
        getResponse(newWord)
        mediaListComponentKey = newWord
      }
    },
  );
</script>