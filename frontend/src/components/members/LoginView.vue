<template>
  <v-container class="d-flex justify-center h-100 align-center" fluid>
    <v-card class="card" elevation="2">
      <v-card-title class="card-title">
        로그인
      </v-card-title>
      아이디
      <v-text-field
        v-model="userId"
        placeholder="pretender@email.co.kr"
        outlined
        dense
        class="text-field"
      />
      비밀번호
      <v-text-field
        v-model="userPassword"
        type="password"
        outlined
        dense
        class="text-field"
      />
      <div id="errorMessage">{{ errorMessage }}</div>
      <v-btn class="button" color="primary" block outlined @click="requestAuth">로그인</v-btn>
      <v-btn class="button" block outlined @click="naverLogin">네이버 로그인</v-btn>
      <v-btn class="button" block outlined @click="kakaoLogin">카카오 로그인</v-btn>
      <v-btn class="button" block outlined @click="googleLogin">구글 로그인</v-btn>
    </v-card>
  </v-container>
</template>

<style>
  .card {
    padding: 16px;
    max-width: 400px;
    width: 100%;
  }
  
  .card-title {
    font-weight: bold;
    text-align: center;
  }
  
  .text-field {
    margin-top: 12px;
  }
</style>

<script setup>
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { useNavigationStore } from '../../composables/stores/navigation';
const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL

// Vue Router와 Pinia Store 초기화
const router = useRouter();
const navigationStore = useNavigationStore();

const route = useRoute();

// 상태 변수
const userId = ref('');
const userPassword = ref('');
const errorMessage = ref('');

// 로그인 요청 함수
async function requestAuth() {
  try {
    console.log('requestAuth...');

    // 로그인 요청
    const response = await axios.get(apiBaseUrl + '/api/login', {
      auth: {
        username: userId.value,
        password: userPassword.value,
      },
      withCredentials: true,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
      },
    });

    // 로그인 성공 처리
    if (response.status === 200) {
      console.log('로그인 성공:', response);

      // 이전 페이지로 리다이렉트
      const previousPage = navigationStore.getPreviousPage() || '/';
      console.log('리다이렉트할 이전 페이지:', previousPage);
      router.push(previousPage);
    } else {
      // 성공이지만 예상치 못한 상태
      errorMessage.value = '예상치 못한 오류가 발생했습니다.';
    }
  } catch (error) {
    console.error('로그인 실패:', error);

    // 오류 처리
    if (!error.response || error.response.status === 401) {
      errorMessage.value = '아이디나 비밀번호가 잘못되었습니다. 다시 확인해주세요.';
    } else {
      errorMessage.value = '로그인 중 오류가 발생했습니다.';
    }
  }
}

async function naverLogin() {
  navigationStore.setPreviousPage(route.fullPath);
  window.location.href = apiBaseUrl + '/api/login/naver'
}

async function kakaoLogin() {
  navigationStore.setPreviousPage(route.fullPath);
  window.location.href = apiBaseUrl + '/api/login/kakao'
}

async function googleLogin() {
  navigationStore.setPreviousPage(route.fullPath);
  window.location.href = apiBaseUrl + '/api/login/google'
}

</script>
