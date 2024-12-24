<template>
    <v-container class="d-flex justify-end">
      <div v-if="isAuthenticated == true">
        <RouterLink to="/myPage">
          마이페이지
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
    <v-container class="d-flex flex-column justify-center" style="height:100%">
        <h1 style="text-align: center;" class="mb-5">pretender</h1>
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

<script setup>
  import { useRouter, useRoute } from 'vue-router';
  import { useDisplay } from 'vuetify';
  import axios from 'axios'
  import { ref, onMounted } from 'vue'
  import { useNavigationStore } from './stores/navigation';

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
    let response = await axios.get(
      'http://localhost:8080/api/authenticated',
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

  // note : v-if하고 같이 쓰려면 onMounted()로 써야 하는 것 같음
  onMounted(async () => {
    isAuthenticated.value = await checkAuthenticated()
  })
</script>