<template>
  <v-app-bar scroll-behavior="hide">
    <v-app-bar-title>
      Pretender
    </v-app-bar-title>
    <v-spacer></v-spacer>
    <div class="mr-3" v-if="isAuthenticated == true">
      <RouterLink  to="/myPage">
        마이페이지
      </RouterLink>
    </div>
    <div class="mr-3" v-else>
      <RouterLink to="/signUp" @click="savePreviousPage">
        회원가입
      </RouterLink> / 
      <RouterLink to="/login">
        로그인
      </RouterLink>
    </div>
  </v-app-bar>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { useNavigationStore } from './stores/navigation';
  import axios from 'axios'

  let isAuthenticated = ref(false)

  onMounted(async () => {
    isAuthenticated.value = await checkAuthenticated()
  })

  async function checkAuthenticated() {
    let response = await axios.get(
      'http://localhost:8080/api/authenticated',
      {
        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ) // axios.get end
    console.log(`authenticated? : ${response.data && response.data.authenticated == true}`)
    return response.data && response.data.authenticated == true
  }

  function savePreviousPage(){
    navigationStore.setPreviousPage(route.fullPath);
  }
</script>