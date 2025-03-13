<template>
    <AppHeader></AppHeader>
    <!-- mediaListComponentKey가 바뀔떄마다 다시 컴포넌트가 렌더링됨 -->
    <MediaList v-show="showMode === 'primary'" :key="mediaListComponentKey" v-bind:mediaInfo="mediaInfo"/>
</template>

<script setup>
  import axios from 'axios'
  import { ref, watch } from 'vue'
  import { useRoute } from 'vue-router';
  import AppHeader from '../AppHeader.vue';
  let route = useRoute()
  let mediaInfo = ref({"tv": [], "movie": [], "allLoaded": false}) // 자식 컴포넌트가 length로 접근하기 때문에 에러 방지
  let mediaListComponentKey = 'initial'
  let showMode = "primary" // primary or more

  // axios 요청
  async function requestMediaList(word) {
    let tvResponse = await axios.get(
      'http://localhost:8080/api/search',
      {
        params : {
          query: word,
          type: "tv"
        }
      }
    ) // axios.get end
    mediaInfo.value.tv = mediaInfo.value.tv.concat(tvResponse.data.results)
    
    let movieResponse = await axios.get(
      'http://localhost:8080/api/search',
      {
        params : {
          query: word,
          type: "movie"
        }
      }
    ) // axios.get end
    mediaInfo.value.movie = mediaInfo.value.movie.concat(movieResponse.data.results)
    console.log("==========mediaInfo=============")
    mediaInfo.value.allLoaded = true
    console.log(mediaInfo.value)
    console.log("==========mediaInfo=============")
  }
  requestMediaList(route.query.word)

  // 쿼리 변경 감지
  watch(
    () => { return route.query.word },
    (newWord) => {
      if (newWord) {
        mediaInfo.value = {"tv": [], "movie": []}
        requestMediaList(newWord)
        mediaListComponentKey = newWord
      }
    },
  );
</script>