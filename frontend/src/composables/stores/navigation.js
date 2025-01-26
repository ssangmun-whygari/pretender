import { defineStore } from 'pinia';
import { ref } from 'vue'

export const useNavigationStore = defineStore('navigation', () => {
  let previousPage = ref('/'); // 이전 페이지
  let activeReply = ref(null); // 대댓글 입력창

  const setPreviousPage = (page) => {
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