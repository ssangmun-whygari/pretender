<template>
  <v-sheet>
    <v-img
      :src="backDropPath"
      height="300px"
      class="mx-auto"
      cover
    ></v-img>
  </v-sheet>
  <h1>제목</h1>
</template>

<script setup>
  import { useMediaDetailStore } from '../stores/MediaDetail';
  import { storeToRefs } from 'pinia';

  const store = useMediaDetailStore()
  const { id, backDropPath } = storeToRefs(store)
  console.log("backDropPath : " + backDropPath.value)
</script>