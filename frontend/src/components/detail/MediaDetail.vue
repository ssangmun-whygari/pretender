<template>
  <v-sheet class="position-relative">
    <v-img
      :src="backDropPath"
      height="600px"
      class="mx-auto"
      cover
    ></v-img>
    <h1 class="position-absolute bottom-0 text-white">
      어쩌고 저쩌고
    </h1>
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