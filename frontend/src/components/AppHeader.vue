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
    <div class="mr-3 d-flex" v-else>
      <SignUpModal></SignUpModal> / 
      <LoginModal 
        :triggered="loginModalTriggered" 
        @requestCheckAuthenticated="executeCheckAuthenticated"
        @requestRestoreTrigger="requestRestoreTrigger"
      >
      </LoginModal>
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
  import { ref, watch, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import { useNavigationStore } from '../composables/stores/navigation';
  import axios from 'axios'

  const emitRestoreTrigger = defineEmits(["requestRestoreTrigger"])
  function requestRestoreTrigger() {
    console.log("AppHeader가 자식 컴포넌트로부터 requestRestoreTrigger 요청을 받음")
    loginModalTriggered.value = false
    emitRestoreTrigger("requestRestoreTrigger")
  }

  // 부모가 내려주는 props
  const props = defineProps({
    loginModalTriggered: {
      type: Boolean,
      default: false
    }
  })
  const loginModalTriggered = ref(false)

  watch(() => {return props.loginModalTriggered}, (bool) => {
    console.log("AppHeader에서 loginModalTriggered props의 변화를 감지함")
    if (bool === true) {
      console.log("loginModalTriggered props 값은 true임")
      loginModalTriggered.value = true // LoginModal에게 props로 내려주는 값
    }
  })

  let isAuthenticated = ref(false)
  let navigationStore = useNavigationStore()
  let route = useRoute()

  async function executeCheckAuthenticated()  {
    isAuthenticated.value = await checkAuthenticated()
  }
  onMounted(executeCheckAuthenticated)

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