<template>
  <v-app>
    <v-main>
      <div :class="['d-flex', 'justify-center', 'align-center', 'transition-image-container', transitionState]" @transitionend="handleTransitionEnd">검색 중...</div>
      <RouterView/>
    </v-main>
  </v-app>
</template>

<style>
  @font-face {
    font-family: 'PartialSansKR-Regular';
    src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2307-1@1.1/PartialSansKR-Regular.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
  }

  .transition-image-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: white;
    font-size: 200px;
    transform: translateX(100%);
    z-index: 10;
    font-family: 'PartialSansKR-Regular', sans-serif;
  }

  .transition-enter {
    transform: translateX(0%);
    transition: transform 0.5s ease;
  }

  .transition-leave {
    transform: translateX(-100%);
    transition: transform 0.5s ease;
  }
</style>

<script setup>
  import { usePageTransition } from './composables/pageTransition'

  const { transitionState, onEnd: handleTransitionEnd } = usePageTransition()
</script>