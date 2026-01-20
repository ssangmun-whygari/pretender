<template>
  <a href="javascript:void(0);" @click="onLogout">로그아웃</a>
</template>

<script setup>
  import axios from 'axios'
  import { useRouter, useRoute } from "vue-router"
  const router = useRouter()
  const route = useRoute()
  const emit = defineEmits(["requestCheckAuthenticated"])
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL

  function isMyPage() {
    return route.path === "/myPage" || route.path.startsWith("/myPage/")
  }

  async function onLogout() {
    const response = await axios.post(`${apiBaseUrl}/logout`, null, {withCredentials: true}); // 주소, body, 옵션
    // alert(JSON.stringify(response.data));
    if (response.data.ok === true) {
      // alert("로그아웃 성공함");
      emit('requestCheckAuthenticated');
      if (isMyPage()) router.replace("/") // 마이페이지에서 로그아웃이면 메인으로
    }
  }
</script>