<template>
  <AppHeader></AppHeader>
  <h1>마이페이지</h1>
  <h2>환영합니다. {{ userId }} 님</h2>
</template>

<script setup>
import AppHeader from '../AppHeader.vue';
import { ref } from 'vue'
import axios from 'axios'

let userId = ref('')

async function getUser() {
  let response = await axios.get(
    'http://localhost:8080/api/authenticated',
    {
      withCredentials: true,

      headers: {
        "X-Requested-With": "XMLHttpRequest"
      }
    }
  ) // axios.get end
  userId.value = response.data.principal.username
}

getUser()

</script>