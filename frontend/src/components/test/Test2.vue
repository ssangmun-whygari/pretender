<template>
  <swiper-container 
    class="mySwiper"
    slides-per-view="4"
    space-between="30"
    centered-slides="true"
  >
    <swiper-slide class="slide">Slide 1</swiper-slide>
    <swiper-slide class="slide">Slide 2</swiper-slide>
    <swiper-slide class="slide">Slide 3</swiper-slide>
    <swiper-slide class="slide">Slide 4</swiper-slide>
    <swiper-slide class="slide">Slide 5</swiper-slide>
    <swiper-slide class="slide">Slide 6</swiper-slide>
    <swiper-slide class="slide">Slide 7</swiper-slide>
  </swiper-container>
  <p class="desc">설명</p>
</template>

<style scoped>
  .mySwiper {
    width: 100%;
    height: 500px;
    background-color: rgb(250, 239, 175);
  }

  .slide {
    background-color: white;
    /* width: 200px; */
  }
</style>

<script setup>
  import { register } from 'swiper/element/bundle';
  register();
  import { onMounted } from 'vue';

  onMounted(() => {
    const swiper = document.querySelector('.mySwiper')

    swiper.addEventListener('swiperslidechange', (event) => {
      console.log('slide changed')
      console.log(event)
      document.querySelector(".desc").innerText = `현재 인덱스 : ${event.target.swiper.activeIndex}`
    })
  })

</script>