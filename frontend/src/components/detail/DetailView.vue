<template>
  <AppHeader 
    :loginModalTriggered="loginModalTriggered"
    @requestRestoreTrigger="executeRestoreTrigger"
  ></AppHeader>
  <!-- 자식에서 "requestOpenLoginModal"라는 emit이 오면 자신 안에 정의된 requestOpenLoginModal() 함수로 보냄 -->
  <MediaDetail @requestOpenLoginModal="requestOpenLoginModel"/>
</template>

<script setup>
  import AppHeader from '../AppHeader.vue';
  import { useNavigationStore } from '@/composables/stores/navigation';
  import { ref } from 'vue'

  // 방문할 때마다 previousPage 초기화
  let navigationStore = useNavigationStore()
  navigationStore.setPreviousPage('/')

  let loginModalTriggered = ref(false)

  function executeRestoreTrigger() {
    console.log("DetailView의 자식 컴포넌트로부터 requestRestoreTrigger 요청을 받음")
    console.log("DetailView의 loginModalTriggered가 false로 복원됨")
    loginModalTriggered.value = false
  }

  function requestOpenLoginModel() {
    console.log("DetailView가 자식 컴포넌트 AppHeader에게 loginModalTriggered를 내려줌")
    loginModalTriggered.value = true
  }
</script>