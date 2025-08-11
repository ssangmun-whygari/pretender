<template>
  <swiper-container
    centered-slides="true"
    autoplay-delay="2500"
    allow-touch-move="false"
  >
      <swiper-slide 
        v-for="(innerItem, index) in imagePaths"
      >
        <div class="image-container">
          <v-img :src="posterBaseUrl + innerItem" class="w-100" cover style="height: 800px;"></v-img>
          <div class="image-overlay"></div>
        </div>
      </swiper-slide>
  </swiper-container>
  <div class="progress-bar d-flex justify-center">
    <svg  width="30%" height="24" aria-label="progress bar">
      <rect x="0" y="0" width="100%" height="12" rx="6" fill="white" />
      <rect id="bar" x="0" y="0" width="0" height="12" rx="6" fill="#1976d2" />
    </svg>
  </div>
</template>

<style scoped>
  .pretender-logo {
    position: absolute;
    top: 40%;
    left: 50%;
    transform: translate(-50%, -50%);
    color: white;
    font-size: 100px;
    z-index: 2;
  }

  .image-container {
    width: 100%;
    height: 100%;
    position: relative;
  }

  .image-overlay {
    width: 100%;
    height: 100%;
    top: 0;
    position: absolute;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 1;
  }

  .progress-bar {
    position: absolute;
    bottom: 5%;
    width: 100%;
    z-index: 2;
  }
</style>

<script setup>
  import {onMounted, nextTick} from 'vue'

  const props = defineProps({
    imagePaths: Array
  })

  const posterBaseUrl = "http://image.tmdb.org/t/p/w780"

  onMounted(() => {
    nextTick(() => {
      const el = document.querySelector("swiper-container")
      const progressBar = document.querySelector("#bar");
      // reference : https://swiperjs.com/element#events
      el.addEventListener("swiperautoplaytimeleft", (e) => {
        const [swiper, time, progress] = e.detail;
        progressBar.setAttribute("width", (1 - progress) * 100 + "%");
      });
    })

  })

</script>