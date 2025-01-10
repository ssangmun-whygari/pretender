<template>
  <h1>로그인</h1>
  <v-text-field label="아이디" v-model="userId"></v-text-field>
  <v-text-field label="비밀번호" type="password" v-model="userPassword"></v-text-field>
  <div id="errorMessage">{{ errorMessage }}</div>
  <v-btn v-on:click="requestAuth">로그인</v-btn>
</template>

<script setup>
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ref } from 'vue';
import { useNavigationStore } from '../stores/navigation';

// Vue Router와 Pinia Store 초기화
const router = useRouter();
const navigationStore = useNavigationStore();

// 상태 변수
const userId = ref('');
const userPassword = ref('');
const errorMessage = ref('');

// 로그인 요청 함수
async function requestAuth() {
  try {
    console.log('requestAuth...');

    // 로그인 요청
    const response = await axios.get('http://localhost:8080/api/login', {
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
</script>
