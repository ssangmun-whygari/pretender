<template>
  <v-app-bar scroll-behavior="hide">
    <v-app-bar-title>
      Pretender
    </v-app-bar-title>
    <v-spacer></v-spacer>
    <!-- reference : https://github.com/vuetifyjs/vuetify/blob/6dc52fe22d4e1dbe25e51abb947b11eb2725a21f/packages/vuetify/src/components/VTextField/VTextField.sass-->
    <div id="search-bar" class="d-flex align-center h-100">
      <v-text-field
        class="w-100 mr-3"
        variant="solo-filled"
        :rounded="true"
        append-inner-icon="mdi-magnify"
        density="compact"
        v-on:keyup.enter="onEnter"
        v-model="word"
      ></v-text-field>
    </div>
    <div class="mr-3" v-if="isAuthenticated == true">
      <RouterLink  to="/myPage">
        마이페이지
      </RouterLink> /
      <RouterLink to="logout" @click="onLogout">
        로그아웃
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

<style>
  #search-bar {
    width: var(--search-bar-length)
  }

  #search-bar .v-input__details {
    display: none;
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
  import { ref, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import { useNavigationStore } from '../composables/stores/navigation';
  import axios from 'axios'

  let isAuthenticated = ref(false)
  let navigationStore = useNavigationStore()
  let route = useRoute()

  onMounted(async () => {
    isAuthenticated.value = await checkAuthenticated()
  })

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
    console.log(`authenticated? : ${response.data && response.data.authenticated == true}`)
    return response.data && response.data.authenticated == true
  }

  function savePreviousPage(){
    navigationStore.setPreviousPage(route.fullPath);
  }

  function onLogout() {
    savePreviousPage()
  }

  import { useDisplay } from 'vuetify';
  import { watch } from 'vue'

  const { lgAndUp } = useDisplay();

  const updateSearchBarLength = (isScreenWidthLong) => {
    if (isScreenWidthLong) {
      document.documentElement.style.setProperty("--search-bar-length", "25%")
    } else {
      document.documentElement.style.setProperty("--search-bar-length", "50%")
    }
  }

  onMounted(() => {
    updateSearchBarLength(lgAndUp.value)
  })

  watch(() => { return lgAndUp.value }, updateSearchBarLength)

  // 검색 기능
  import { useRouter } from 'vue-router';
  let word = ref('')
  const router = useRouter()
  const onEnter = async () => {
    router.push({
            path: '/search',
            query: {
              word: word.value
            }
          })
  }

</script>