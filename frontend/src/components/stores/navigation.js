import { defineStore } from 'pinia';

export const useNavigationStore = defineStore('navigation', {
    state: () => ({
        previousPage: '/', // 이전페이지 기본값
    }),
    actions: {
        setPreviousPage(page){
            this.previousPage = page;
        },
    },
});