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
      <LogoutLink @requestCheckAuthenticated="executeCheckAuthenticated"/>
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
  import { useRoute, useRouter } from 'vue-router'
  import { useNavigationStore } from '../composables/stores/navigation';
  import axios from 'axios'
  import { useDisplay } from 'vuetify';
  import { useCheckAuthenticated } from '@/composables/checkAuthenticated';
  import LogoutLink from './members/LogoutLink.vue';

  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const { lgAndUp } = useDisplay();
  const emitRestoreTrigger = defineEmits(["requestRestoreTrigger"])
  const props = defineProps({
    loginModalTriggered: {
      type: Boolean,
      default: false
    }
  })
  const loginModalTriggered = ref(false)
  const isAuthenticated = ref(false)
  const navigationStore = useNavigationStore()
  const route = useRoute()
  const router = useRouter()
  const word = ref('') // 검색기능

  function requestRestoreTrigger() {
    console.log("AppHeader가 자식 컴포넌트로부터 requestRestoreTrigger 요청을 받음")
    loginModalTriggered.value = false
    emitRestoreTrigger("requestRestoreTrigger")
  }

  async function executeCheckAuthenticated()  {
    isAuthenticated.value = await useCheckAuthenticated()
  }

  /* deprecated
  async function onLogout() {
    const response = await axios.post(`${apiBaseUrl}/logout`, null, {withCredentials: true}); // 주소, body, 옵션
    alert(JSON.stringify(response.data));
    if (response.data.ok === true) {
      alert("로그아웃 성공함");
      isAuthenticated.value = false;
    }
  }
  */

  const updateSearchBarLength = (isScreenWidthLong) => {
    if (isScreenWidthLong) {
      document.documentElement.style.setProperty("--search-bar-length", "25%")
    } else {
      document.documentElement.style.setProperty("--search-bar-length", "50%")
    }
  }

  const onEnter = async () => {
    router.push({
            path: '/search',
            query: {
              word: word.value
            }
          })
  }

  onMounted(executeCheckAuthenticated)
  watch(() => {return props.loginModalTriggered}, (bool) => {
    console.log("AppHeader에서 loginModalTriggered props의 변화를 감지함")
    if (bool === true) {
      console.log("loginModalTriggered props 값은 true임")
      loginModalTriggered.value = true // LoginModal에게 props로 내려주는 값
    }
  })
  onMounted(() => {
    updateSearchBarLength(lgAndUp.value)
  })
  watch(() => { return lgAndUp.value }, updateSearchBarLength)

</script>