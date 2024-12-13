<template>
  <h1 v-show="props.mediaInfo.length > 0">tv 쇼</h1> <!--v-if 어째선지 안됨...-->
  <v-container v-if="mediaInfoReady">
    <v-row v-for="i in rowNumber"> <!--1, 2, 3...-->
      <v-col v-for="j in colList(i)"cols="4" class="ga-3">
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
 // v-on:click.prevent="handleClick(index(i, j))"
  import { computed } from 'vue'
  const itemsPerRow = 3
  const posterBaseUrl = "http://image.tmdb.org/t/p/w500/"
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
  const props = defineProps({
    mediaInfo: Array
  })
  const rowNumber = computed(() => {
    return Math.floor(props.mediaInfo.length / itemsPerRow) + 1
  })
  const colList = (row) => {
    if (props.mediaInfo.length % itemsPerRow == 0) {
      return itemsPerRow
    } else {
      if (Math.floor(props.mediaInfo.length / itemsPerRow) == (row - 1)) {
        return props.mediaInfo.length % itemsPerRow // 끝줄
      } else {
        return itemsPerRow
      }
    }
  }
  const index = (row, col) => {
    return (row - 1) * itemsPerRow + (col - 1)
  }
  const title = (index) => {
    return props.mediaInfo[index]["title"] ? props.mediaInfo[index]["title"] : props.mediaInfo[index]["name"]
  }
  const posterPath = (index) => {
    return props.mediaInfo[index]["poster_path"] ? (posterBaseUrl + props.mediaInfo[index]["poster_path"]) : noImageUrl
  }
  const backDropPath = (index) => {
    return props.mediaInfo[index]["backdrop_path"] ? (posterBaseUrl + props.mediaInfo[index]["backdrop_path"]) : noImageUrl
  }
  const mediaId = (index) => {
    return props.mediaInfo[index]["id"] ? props.mediaInfo[index]["id"] : -1
  }
  const mediaInfoReady = computed(() => {
    return props.mediaInfo.length > 0
  })

  import { useMediaDetailStore } from '../stores/MediaDetail'
  const store = useMediaDetailStore()
  const handleClick = (index) => {
    store.setMediaDetail({
      id: mediaId(index),
      backDropPath: backDropPath(index)
    })
  }
</script>