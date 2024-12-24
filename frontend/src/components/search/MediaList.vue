<template>
  <h1 v-show="mediaInfo.length > 0">tv 쇼</h1> <!--v-if 어째선지 안됨...-->
  <v-container v-if="mediaInfo.length > 0">
    <v-row v-for="i in rowNumber()"> <!--1, 2, 3...-->
      <v-col v-for="j in colList(i)" cols="4" class="ga-3">
        <v-card elevation="10">
          <RouterLink 
            :to="{path: '/detail', query: {id : mediaId(index(i, j))}}"
            v-on:click.prevent="handleClick(index(i, j))"
          >
          <v-img
            :src="posterPath(index(i, j))"
            height="300px"
            class="mx-auto"
          ></v-img>
          <h1>{{ title(index(i, j))}}</h1>
        </RouterLink></v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style>
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
  import { computed } from 'vue'
  const itemsPerRow = 3
  const posterBaseUrl = "http://image.tmdb.org/t/p/w500/"
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
  const props = defineProps({
      mediaInfo: Object
    })

  const mediaInfo = computed(() => {
    console.log("computed.....")
    console.log(props.mediaInfo)
    console.log(props.mediaInfo.tv)
    console.log("computed..... end")
    return props.mediaInfo.tv
  })
  const rowNumber = () => {
    console.log("rowNumber...")
    console.log(mediaInfo)
    console.log(mediaInfo.value.length)
    console.log("rowNumber...")
    return (mediaInfo.value.length % itemsPerRow == 0) ? 
       Math.floor(mediaInfo.value.length / itemsPerRow) :
       Math.floor(mediaInfo.value.length / itemsPerRow) + 1
  }
  const colList = (row) => {
    if (mediaInfo.value.length % itemsPerRow == 0) {
      return itemsPerRow
    } else {
      if (Math.floor(mediaInfo.value.length / itemsPerRow) == (row - 1)) {
        return mediaInfo.value.length % itemsPerRow // 끝줄
      } else {
        return itemsPerRow
      }
    }
  }
  const index = (row, col) => {
    return (row - 1) * itemsPerRow + (col - 1)
  }
  const title = (index) => {
    return mediaInfo.value[index]["title"] ? mediaInfo.value[index]["title"] : mediaInfo.value[index]["name"]
  }
  const posterPath = (index) => {
    return mediaInfo.value[index]["poster_path"] ? (posterBaseUrl + mediaInfo.value[index]["poster_path"]) : noImageUrl
  }
  const backDropPath = (index) => {
    return mediaInfo.value[index]["backdrop_path"] ? (posterBaseUrl + mediaInfo.value[index]["backdrop_path"]) : noImageUrl
  }
  const mediaId = (index) => {
    return mediaInfo.value[index]["id"] ? mediaInfo.value[index]["id"] : -1
  }
  const mediaInfoReady = () => {
    return mediaInfo.value.length > 0
  }

  // TODO : id도 굳이 pinia에 저장할 필요 있나?
  // TODO : "movie"인지, "tv"인지를 저장해야 함(그것에 따라 요청해야 하는 외부 api가 다름)
  import { useMediaDetailStore } from '../stores/MediaDetail'
  const store = useMediaDetailStore()
  const handleClick = (index) => {
    store.setMediaDetail({
      id: mediaId(index),
      backDropPath: backDropPath(index)
    })
  }
</script>