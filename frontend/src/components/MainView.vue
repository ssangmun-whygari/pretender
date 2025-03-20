<template>
  <div class="background-container"></div>
  <v-container class="d-flex justify-end position-relative">
    <div v-if="isAuthenticated == true">
      <RouterLink to="/myPage">
        마이페이지
      </RouterLink> / 
      <RouterLink to="logout" @click="onLogout">
        로그아웃
      </RouterLink>
    </div>
    <div v-else>
      <RouterLink to="/signUp" @click="savePreviousPage">
        회원가입
      </RouterLink> / 
      <RouterLink to="/login">
        로그인
      </RouterLink>
    </div>
  </v-container>
  <!-- flex와 grid 함께 사용하지 말기 (어째선지 제대로 정렬이 안됨)-->
  <v-container class="d-flex flex-column justify-center position-relative" style="height:100%">
      <h1 style="text-align: center;" class="mb-5">pretender</h1>
      <PopularMovieCarousel/>
      <div>
        <v-text-field
          class="mx-auto main-search-bar"
          :max-width="lgAndUp ? 1000 : null"
          placeholder="제목을 검색하세요"
          v-on:keyup.enter="onEnter"
          v-model="word"
        ></v-text-field>
      </div>
  </v-container>
</template>

<style scoped>
  .background-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw; /* 화면 너비 100% */
    height: 100vh; /* 화면 높이 100% */
    /* background-image: linear-gradient(rgb(255, 255, 255),rgba(255, 255, 255, 0.205)), url('http://localhost:8080/resource/backgroundImage'); */
    background-size: cover;
    background-position: top;
    background-attachment: fixed;
    overflow: hidden;
  }

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
  import { useRouter, useRoute } from 'vue-router';
  import { useDisplay } from 'vuetify';
  import axios from 'axios'
  import { ref, onMounted, nextTick } from 'vue'
  import { useNavigationStore } from '../composables/stores/navigation';
  import PopularMovieCarousel from './PopularMovieCarousel.vue';
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const backgroundImageUrl = apiBaseUrl + '/resource/backgroundImage'

  onMounted(() => {
    // console.log("+++++++++++++++++++++onMounted()...")
    // console.log(backgroundImageUrl)
    // console.log(`linear-gradient(rgb(255, 255, 255), rgba(255, 255, 255, 0.205)), url('${backgroundImageUrl}');`)
    // console.log(document.querySelector('.background-container'))
    // console.log("+++++++++++++++++++++onMounted()end")
    document.querySelector('.background-container').style.backgroundImage = 
    `linear-gradient(rgb(255, 255, 255), rgba(255, 255, 255, 0.205)), url('${backgroundImageUrl}')`
  })


  const { lgAndUp } = useDisplay();
  const router = useRouter();
  const route = useRoute();
  const navigationStore = useNavigationStore();

  let word = ref('')
  let responseData = ref(null)
  let isAuthenticated = ref(false)

  async function onEnter() {
      router.push({
            path: '/search',
            query: {
              word: word.value
            }
          })
  }

  async function checkAuthenticated() {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
    let response = await axios.get(
      apiBaseUrl + '/api/authenticated',
      {
        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ) // axios.get end
    return response.data && response.data.authenticated == true
  }

  function savePreviousPage(){
    navigationStore.setPreviousPage(route.fullPath);
  }

  function onLogout() {
    savePreviousPage()
  }

  // note : v-if하고 같이 쓰려면 onMounted()로 써야 하는 것 같음
  onMounted(async () => {
    isAuthenticated.value = await checkAuthenticated()
  })
</script>