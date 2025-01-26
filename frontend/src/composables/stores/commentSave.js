// src/stores/commentSave.js
import { defineStore } from 'pinia'

export const useCommentSaveStore = defineStore('commentSave', {
  state: () => ({
    draft: {
      content : '',
      parentNo : null,
      mediaId : null,
      targetElementId : null,
      page : null,
      setOrder : null,
    }
  }),
  actions: {
    setDraft(content, parentNo, mediaId, targetElementId,page,sortOrder) {
      this.draft = { content, parentNo, mediaId, targetElementId, page, sortOrder }
    },
    clearDraft() {
      this.draft = { content: '', parentNo: null, mediaId: null, targetElementId: null, page: null, sortOrder: null }
    }
  }
})