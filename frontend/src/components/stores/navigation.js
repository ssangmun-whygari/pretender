import { defineStore } from 'pinia';

export const useNavigationStore = defineStore('navigation', {
  state: () => ({
    previousPage: '/', // 이전 페이지 기본값
    activeReply: null, // 대댓글 입력창 상태 저장
  }),
  actions: {
    setPreviousPage(page) {
        // console.log('이전 페이지 설정:', page);
      this.previousPage = page;
    },
    getPreviousPage() {
        return this.previousPage || '/';
      },
    goToPreviousPage(router) {
      router.push(this.previousPage || '/');
    },
  },
});
