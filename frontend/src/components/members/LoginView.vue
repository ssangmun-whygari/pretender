<template>
  <h1>로그인</h1>
  <v-text-field label="아이디" v-model="userId"></v-text-field>
  <v-text-field label="비밀번호" v-model="userPassword"></v-text-field>
  <div id="errorMessage"></div>
  <v-btn v-on:click="requestAuth">로그인</v-btn>
</template>

<script setup>
// TODO : axios로 HTTP Basic 헤더 포함 인증 요청 보내기
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ref } from 'vue'
const router = useRouter()

let userId = ref('')
let userPassword = ref('')
let errorMessage = ref('')

function requestAuth() {
    console.log("requestAuth...")
    let response = axios.get(
      'http://localhost:8080/api/login',
      {
        auth: {
          username: userId.value,
          password: userPassword.value
        },

        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ).then(response => {
      document.querySelector("#errorMessage").innerHTML 
          = "로그인 성공"
      router.push({
        path: '/'
      })
    }).catch(error => {
      console.log(error.response)
      if (!error.response) {
        document.querySelector("#errorMessage").innerHTML 
          = "아이디나 비밀번호가 잘못되었습니다. 다시 확인해주세요"
      }
      if (error.response && error.response.status == 401) {
        document.querySelector("#errorMessage").innerHTML 
          = "아이디나 비밀번호가 잘못되었습니다. 다시 확인해주세요"
      }
    })
}

</script>