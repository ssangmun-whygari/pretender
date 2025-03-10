import { defineStore } from 'pinia';

export const useReviewSaveStore = defineStore('reviewSave', () => {
  let draft = {
    content : ""
  }

  const setDraft = (text) => {
    draft.content = text
  }

  const clearDraft = () => {
    draft.content = ""
  }

  return {
    draft,
    setDraft,
    clearDraft
  };
});