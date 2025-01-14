import { defineStore } from 'pinia';
import { ref } from 'vue'

// Composition API 스타일로 변경함
export const useNavigationStore = defineStore('navigation', () => {
  let previousPage = ref('/'); // 이전 페이지 기본값
  let activeReply = ref(null); // 대댓글 입력창 상태 저장

  const setPreviousPage = (page) => {
    // console.log('이전 페이지 설정:', page);
    previousPage.value = page;
  };

  const getPreviousPage = () => {
    return previousPage.value || '/';
  };

  const goToPreviousPage = (router) => {
    router.push(previousPage.value || '/');
  };

  return {
    previousPage,
    activeReply,
    setPreviousPage,
    getPreviousPage,
    goToPreviousPage,
  };
});