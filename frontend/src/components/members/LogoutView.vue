<template>

</template>

<script setup>
import axios from 'axios';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router'
import { useNavigationStore } from '@/composables/stores/navigation';

let navigationStore = useNavigationStore()
let router = useRouter()

async function logout() {
  try {
    let response = await axios.post(
      'http://localhost:8080/logout',
      null, // 본문
      {
          withCredentials: true,
          headers: {
            "X-Requested-With": "XMLHttpRequest"
          },
      }
    )
    // console.log("logout 응답 : ")
    // console.log(response)
    // return
  } catch (error) {
    console.error('로그아웃 실패:', error);
    // TODO : 400번대, 500번대 에러에 대하여 오류 페이지 보여주고 홈으로 가는 링크 만들기
  }

  let previousPage = navigationStore.getPreviousPage()
  let nextPath = ''
  // 마이페이지에서 로그아웃하면 메인화면으로 보내야 함
  if (/^\/myPage/.test(previousPage)) {
    nextPath = '/'
  } else {
    nextPath = previousPage
  }
  router.push({
    path: nextPath
  })
}

logout()
</script>