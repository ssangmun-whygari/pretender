<template>
  <div>
    <div>
      <h2>댓글 {{ totalComments }}개</h2>
          <div class="sort-container">
              <v-select
                id="sort"
                v-model="sortOrder"
                :items="sortOptions"
                item-value="value"
                item-title="label"
                variant="outlined"
                label="정렬"
                class="select_option my-select"
              ></v-select>
          </div>
      <ul class="comment-list">
        <li v-for="comment in comments" :key="comment.no" class="comment-item">
          <img :src="'http://localhost:8080/api/members/profile/image?memberId=' + comment['members_id']" alt="프로필" class="comment-image" />
          <div class="comment-content">
            <div class="comment-header">
              <div class="nicknameTime">
                <span class="nickname">{{ comment.nickname }}</span>
                <span v-if="comment.correct_date">
                  (수정됨: {{ formatDate(comment.correct_date) }})
                </span>
                <span v-else class="time">{{ formatDate(comment.post_date) }}</span>
                </div>                
                <div v-if="comment.stars" class="stars-container">
                  <!-- 별점 표시 -->
                <v-rating
                  class="stars"
                  :model-value="comment.stars"
                  :length="5"
                  half-increments
                  hover
                  active-color="orange"
                  size="24"
                  readonly
                />
              </div>
                <div class="dropdown-container">
                  <button class="dots-btn" @click="toggleDropdown(comment)">
                    &#x22EE;
                  </button>
                  <div v-if="activeDropdown === comment.no" class="dropdown-menu">
                    <button @click="enableEditMode(comment)" class="dropdown-item">
                      ✏ 수정
                    </button>
                    <button @click="deleteComment(comment)" class="dropdown-item">
                      🗑 삭제
                    </button>
                </div>
              </div>
            </div>
          
            <!-- 댓글 수정 가능 -->
            <div v-if="comment.isEditing">
              <textarea
                v-model="comment.updatedContent"
                class="edit-textarea"
                :style="{ width: '100%' }"
              ></textarea>
              <div class="reply-actions">
                <button @click="cancelEditComment(comment)" class="cancel-btn">취소</button>
                <button @click="saveEditComment(comment)" class="submit-btn">저장</button>
              </div>
            </div>

            <!-- 댓글 읽기 모드 -->
            <div v-else>
              <p v-if="comment.is_deleted ==='N'" class="content">
                {{ comment.content }}
              </p>
              <p v-else class="content_deleted">
                삭제된 댓글입니다.
              </p>
            </div>
            <div class="comment-actions">
            <!-- 좋아요 버튼 -->
            <button
              class="like-btn"
              :class="{ liked: likedCommentIds.includes(comment.no) }"
              @click="toggleLike(comment.no)"
            >
              👍 {{ formatLikeCount(comment.likeCount) }}
            </button>

            <!-- 답글 버튼 -->
            <button class="reply-btn" @click="toggleReplies(comment)">
              답글
              <span v-if="comment.replyCount > 0">{{ comment.replyCount }}개 보기</span>
            </button>
          </div>


          <!-- 대댓글 목록 -->
          <ul v-if="replies[comment.no]" class="reply-list">
            <li v-for="(reply, index) in replies[comment.no]" :key="reply.no" class="comment-item">
              {{index + 1}}<!-- to peachea27 : 테스트용, 이 숫자는 나중에 지워--><img :src="'http://localhost:8080/api/members/profile/image?memberId=' + reply['members_id']" alt="프로필" class="comment-image" />
              <div class="comment-content">
                <div class="comment-header">
                  <div class="nicknameTime">
                    <span class="nickname">{{ reply.nickname }}</span>
                    <span v-if="reply.correct_date && reply.is_deleted =='N'">
                      (수정됨: {{ formatDate(reply.correct_date) }})
                    </span>
                    <span v-if="reply.is_deleted =='N'" class="time">
                      {{ formatDate(reply.post_date) }}</span>
                  </div>
                  <div class="dropdown-container">
                    <button class="dots-btn" @click="toggleDropdown(reply)">
                      &#x22EE;
                    </button>
                    <div v-if="activeDropdown === reply.no" class="dropdown-menu">
                      <button @click="enableEditMode(reply)" class="dropdown-item">
                        ✏ 수정
                      </button>
                      <button @click="deleteComment(reply)" class="dropdown-item">
                        🗑 삭제
                      </button>
                    </div>
                  </div>
                </div>
                  <div v-if="reply.isEditing">
                      <textarea
                      v-model="reply.updatedContent"
                      class="edit-textarea"
                      :style="{ width: '100%' }"
                    ></textarea>
                    <div class="reply-actions">
                      <button @click="cancelEditComment(reply)" class="cancel-btn">취소</button>
                      <button @click="saveEditComment(reply)" class="submit-btn">저장</button>
                    </div>
                  </div>
                  <div v-else>
                    <p v-if="reply.is_deleted ==='Y'" class="content_deleted">
                      삭제된 댓글입니다.
                    </p>
                    <p v-else class="content">
                      {{ reply.content }}
                    </p>
                  </div>
                <div class="comment-actions">
                  <!-- 좋아요 버튼 -->
                  <button
                    class="like-btn"
                    :class="{ liked: likedCommentIds.includes(reply.no) }"
                    @click="toggleLike(reply.no)"
                  >
                    👍 {{ formatLikeCount(reply.likeCount) }}
                  </button>
                </div>
              </div>
            </li>
            <v-btn
              v-if="hasMoreReplies[comment.no]"
              @click="loadMoreReplies(comment.no)"
              class="load-more-btn"
            >
              더보기
          </v-btn>
       <!-- 대댓글 입력 창 -->
              <div class="reply-input-container">
                <textarea
                placeholder="답글을 입력하세요..."
                class="reply-textarea"
                v-model="comment.replyText"
                @input="handleInput"
                @keydown.enter="handleEnterKey(comment)"
              ></textarea>
              <div class="reply-actions">
                <v-btn
                  @click="clearReplyText(comment)"
                  class="cancel-btn">
                  취소
              </v-btn>
                <v-btn
                  @click="submitReply(comment)"
                  class="submit-btn"
                  :disabled="!comment.replyText.trim()"
                >
                 답글</v-btn>
                </div>
              </div>
           </ul>
            
          </div>
        </li>
      </ul>
    </div>
    <v-pagination 
    v-model="currentPage"
    :length="totalPages"
    @update:modelValue="onPageChange"
  />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import { useNavigationStore } from '../../composables/stores/navigation';

const comments = ref([]);
const replies = ref([]);
const repliesPage = ref({});
const hasMoreReplies = ref({});
const likedCommentIds = ref([]); // 사용자가 좋아요한 댓글 ID 목록
const totalComments = ref(0); // 전체 댓글 수
const totalPages = ref(0);
const currentPage = ref(1);
const isLoading = ref(true); // 로딩 상태 관리
const error = ref(null); // 오류 메시지 관리
const activeDropdown = ref(null); // 활성화된 드롭다운 ID
const loggedInUserId = ref(null);

const route = useRoute();
const contentId = ref(route.query.id ||null);

const router = useRouter();
const navigationStore = useNavigationStore(); // Pinia 스토어 초기화
const sortOrder = ref("likeCount");
const sortOptions = [
  { label: '좋아요순', value: 'likeCount' },
  { label: '최신순', value: 'no' },
  { label: '댓글많은순', value: 'replyCount' },
];

// 로그인 검증 함수
async function checkAuthenticated() {
  try {
    let response = await axios.get('http://localhost:8080/api/authenticated', {
      withCredentials: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
      },
    });
    return response.data && response.data.authenticated === true;
  } catch (err) {
    console.error('로그인 검증 중 오류가 발생했습니다:', err);
    return false;
  }
}

const fetchLoggedInUserId = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/getLoggedInId", {
      withCredentials: true,
    });
    console.log(response.data);
    loggedInUserId.value = response.data; // 서버에서 반환된 로그인 사용자 ID
  } catch (error) {
    console.error("사용자 정보를 가져오는 중 오류 발생:", error.response?.data || error.message);
  }
};

// 좋아요한 댓글 ID 가져오기
const fetchLikedCommentIds = async (contentId) => {
  const isAuthenticated = await checkAuthenticated();
  try {
    if (!isAuthenticated) {
      return;
    }    
    const response = await axios.get('http://localhost:8080/api/myReviewLikes', {
      params: { contentId },
      withCredentials: true,
    });
    likedCommentIds.value = response.data;
  } catch (err) {
    console.error('좋아요한 댓글 정보를 불러오는 데 실패했습니다:', err.response || err.message);
  }
};


// 좋아요 토글 함수
const toggleLike = async (commentId) => {
  try {
    let comment = comments.value.find(c => c.no === commentId);

    // 대댓글인지 확인
    if (!comment) {
      for (const replyList of Object.values(replies.value)) {
        comment = replyList.find(r => r.no === commentId);
        if (comment) break;
      }
    }

    if (!comment || !contentId.value) {
      console.error("댓글이나 contentId를 찾을 수 없습니다.");
      return;
    }

    const isAuthenticated = await checkAuthenticated();
    if (!isAuthenticated) {
      navigationStore.setPreviousPage(router.currentRoute.value.fullPath);
      router.push('/login');
      return;
    }

    if (likedCommentIds.value.includes(commentId)) {
      // 좋아요 취소
      const response = await axios.delete('http://localhost:8080/api/reviewUnlike', {
        params: { id: contentId.value, no: commentId },
        withCredentials: true,
      });

      if (response.status === 200) {
        likedCommentIds.value = likedCommentIds.value.filter(id => id !== commentId);
        comment.likeCount -= 1;
      }
    } else {
      // 좋아요 추가
      const response = await axios.post('http://localhost:8080/api/reviewLike', {
        mediaId: contentId.value,
        reviewsNo: commentId,
      }, { withCredentials: true });

      if (response.status === 200) {
        likedCommentIds.value.push(commentId);
        comment.likeCount += 1;
      }
    }
  } catch (error) {
    console.error('좋아요 토글 중 오류 발생:', error.response || error.message);
  }
};

// 드롭다운 토글 함수
const toggleDropdown = (item) => {
  if (item.members_id === loggedInUserId.value) {
    activeDropdown.value = activeDropdown.value === item.no ? null : item.no;
  }
  if (item.is_deleted ==='Y'){
    activeDropdown.value = null;
  }
};

// 바깥쪽 클릭 감지 함수
const handleOutsideClick = (event) => {
  const dropdownContainers = document.querySelectorAll('.dropdown-container');
  let isClickInsideDropdown = false;

  dropdownContainers.forEach((container) => {
    if (container.contains(event.target)) {
      isClickInsideDropdown = true;
    }
  });

  if (!isClickInsideDropdown) {
    activeDropdown.value = null; // 드롭다운 닫기
  }
};
// 수정 모드 활성화
const enableEditMode = (item) => {
  item.isEditing = true;
  activeDropdown.value = null; // 드롭다운 닫기
  item.updatedContent = item.content; // 기존 내용을 수정 상태로 설정
};

// 수정 취소
const cancelEditComment = (item) => {
  item.isEditing = false;
  item.updatedContent = ""; // 수정 내용을 초기화
};

// 수정 저장
const saveEditComment = async (item) => {
  try {
    if (!item.updatedContent.trim()) {
      alert("내용을 입력해주세요.");
      return;
    }

    // 서버로 수정 요청
    const response = await axios.put(
      "http://localhost:8080/api/modifyReview",
      {
        no: item.no, // 댓글 또는 대댓글 ID
        membersId: item.members_id,
        content: item.updatedContent, // 수정된 내용
        id: contentId.value, // 게시물 ID
        isDeleted: item.is_deleted
      },
      { withCredentials: true }
    );

    if (response.status === 200) {
      // 수정 성공 시 UI 업데이트
      item.content = item.updatedContent;
      item.isEditing = false;
    }
  } catch (error) {
    console.error("댓글 수정 중 오류 발생:", error.response?.data || error.message);
  }
};


// 댓글 삭제 함수
const deleteComment = async (item) => {
  try {
    // 삭제 확인 대화상자
    const response = await axios.put("http://localhost:8080/api/deleteReview", null, {
      params: {
        id: contentId.value, // 게시물 ID
        no: item.no, // 댓글 ID
        membersId: item.members_id
      },
      withCredentials: true,
    });

    // 성공적으로 삭제된 경우
    if (response.status === 200) {
      console.log("댓글 삭제 성공:", response.data);

       // UI에서 삭제된 댓글로 표시
       const commentToDelete = comments.value.find((comment) => comment.no === item.no);
      if (commentToDelete) {
        commentToDelete.is_deleted = "Y"; // 상태를 "삭제됨"으로 표시
      }

      // 대댓글 처리
      for (const parentId in replies.value) {
        const replyToDelete = replies.value[parentId].find((reply) => reply.no === item.no);
        if (replyToDelete) {
          replyToDelete.is_deleted = "Y"; // 상태를 "삭제됨"으로 표시
        }
      }
      activeDropdown.value = null; // 드롭다운 닫기
    }
  } catch (error) {
    console.error(
      "댓글 삭제 중 오류 발생:",
      error.response?.data || error.message
    );
    alert("댓글 삭제에 실패했습니다. 다시 시도해주세요.");
  }
};

// 날짜 포맷 함수
const formatDate = (date) => {
  if (!date) return "";

  const now = new Date();
  const diff = Math.floor((now - new Date(date)) / 1000); // 차이를 초 단위로 계산

  if (diff < 60) return `${diff}초 전`; // 1분 미만
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`; // 1시간 미만
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`; // 24시간 미만
  if (diff < 30 * 86400) return `${Math.floor(diff / 86400)}일 전`; // 30일 미만
  if (diff < 365 * 86400) return `${Math.floor(diff / (30 * 86400))}개월 전`; // 1년 미만
  return `${Math.floor(diff / (365 * 86400))}년 전`; // 1년 이상
};


// 좋아요 숫자 포맷 함수
const formatLikeCount = (count) => {
  if (!count) {
    return null;
  }
  if (count >= 10000) return `${(count / 10000).toFixed(1)}만`;
  return count.toString();
};

// 댓글 가져오기 함수
const fetchComments = async (contentId, page = 0, sortBy = "likeCount") => {
  try {
    const response = await axios.get(`http://localhost:8080/api/comments`, {
      params: { id: contentId, page, sortBy }, // id와 sortBy를 쿼리 파라미터로 전달
    });

    if (response.data && response.data.comments) {
      response.data.comments.forEach((comment) => {
        comment.replyText = ""; // 각 댓글에 replyText 추가
      });
      comments.value = response.data.comments; // 댓글 목록 저장
      totalComments.value = response.data.totalComments || 0; // 전체 댓글 수 저장
      totalPages.value = response.data.totalPages || 0;
    }
  } catch (err) {
    console.error("API 호출 중 오류가 발생했습니다:", err);
    error.value = "댓글 정보를 불러오는 데 실패했습니다.";
  } finally {
    isLoading.value = false; // 로딩 상태 해제
  }
};

watch(sortOrder, (newValue) => {
  console.log(`정렬 옵션 변경: ${newValue}`);
  replies.value = {};
  fetchComments(contentId.value,0, newValue); // 새 정렬 옵션으로 댓글 갱신
  currentPage.value = 1;
});

const onPageChange = (page) => {
  currentPage.value = page; // 페이지 업데이트
  fetchComments(contentId.value, page - 1, sortOrder.value); // 서버에서 새 데이터 가져오기
};

// 대댓글 가져오기 함수
const fetchReplies = async (parentNo, page = 0) => {
  try {
    const response = await axios.post('http://localhost:8080/api/replies', null, {
      params: { id: contentId.value, page, parentId: parentNo },
    });

   const fetchedReplies = response.data.replies;

    // Append new replies to the existing list
    if (!replies.value[parentNo]) {
      replies.value[parentNo] = [];
    }
    replies.value[parentNo] = [...replies.value[parentNo], ...fetchedReplies];

    // Update the page and "has more" status
    repliesPage.value[parentNo] = page + 1;
    hasMoreReplies.value[parentNo] = fetchedReplies.length === 20; // Assume 20 replies per page
  } catch (err) {
    console.error('대댓글 불러오기 실패:', err);
  }
};

const loadMoreReplies = async (parentNo) => {
  const nextPage = repliesPage.value[parentNo] || 0;
  await fetchReplies(parentNo, nextPage);
};

// 대댓글 토글 함수
const toggleReplies = async (comment) => {
  if (replies.value[comment.no]) {
    delete replies.value[comment.no];
  } else {
    await fetchReplies(comment.no);
  }
};

// 대댓글 입력 취소
const clearReplyText = (comment) => {
  comment.replyText = ""; // 취소 시 텍스트 초기화
};

// 대댓글 제출
const submitReply = async (comment) => {
  const isAuthenticated = await checkAuthenticated(); // 로그인 상태 확인

  if (!isAuthenticated) {
    // 로그인되지 않은 경우
    navigationStore.setPreviousPage(router.currentRoute.value.fullPath); // 현재 페이지 저장
    router.push('/login'); // 로그인 페이지로 이동
    return;
  }

  if (!comment.replyText.trim()) return;

  try {
    const response = await axios.post('http://localhost:8080/api/insertReview', {
      parent_no: comment.no,
      content: comment.replyText,
    },{
      params:{ id: contentId.value },
      withCredentials: true, // 인증 정보를 포함하도록 설정
    });
    console.log('서버에서 반환된 데이터:', response.data);
   
    comment.replyText = '';
    
    replies.value[comment.no] = [];
    await fetchReplies(comment.no);

  } catch (err) {
    if (err.response) {
    console.error('대댓글 추가 중 오류가 발생했습니다:', err.response.data);
  } else {
    console.error('대댓글 추가 중 알 수 없는 오류가 발생했습니다:', err.message);
  }
  }
};

const handleEnterKey = (comment) => {
  if (comment.replyText.trim()) {
    submitReply(comment); // 대댓글 제출
  }
};

// 컴포넌트 마운트 시 API 호출
onMounted(async () => {

  document.addEventListener('click', handleOutsideClick);

    // 이전 페이지 설정
    const currentPath = router.currentRoute.value.fullPath;
    console.log('현재 페이지:', currentPath);

  if (!navigationStore.getPreviousPage() || navigationStore.getPreviousPage() === '/') {
    navigationStore.setPreviousPage(currentPath); // 현재 페이지를 이전 페이지로 설정
    console.log('리다이렉트할 이전 페이지 설정:', currentPath);
  }

  if (contentId) {
    await fetchLikedCommentIds(contentId.value); // 좋아요한 댓글 ID 가져오기
    await fetchComments(contentId.value,0,sortOrder.value); // 댓글 불러오기
  } else {
    console.error('contentId가 없습니다.');
  }

     // 대댓글 초기화
     comments.value.forEach((comment) => {
      repliesPage.value[comment.no] = 0;
      hasMoreReplies.value[comment.no] = true;
    });

  await fetchLoggedInUserId();
 
});


onBeforeUnmount(() => {
  document.removeEventListener('click', handleOutsideClick); // 이벤트 해제
});


</script>

<style scoped>
.reply-input-container {
  margin-top: 10px;
}

.reply-textarea {
  width: 100%;
  height: 60px;
  margin-bottom: 10px;
  padding: 10px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn,
.submit-btn {
  padding: 5px 10px;
  border: none;
  cursor: pointer;
}

.cancel-btn {
  background-color: #ccc;
  color: #999;
}

.submit-btn {
  background-color: #007bff;
  color: #fff;
}

.cancel-btn.active {
  color: #000;
}

.comment-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.comment-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.comment-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 16px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nicknameTime{
  display: flex;
  gap: 16px;
  padding-right: 16px;
}

.time{
  color: #888;
  font-size: 14px;
}

.reply-list {
  list-style: none;
  margin: 8px 0 0 56px;
  padding: 0;
}


.comment-actions {
  display: flex;
  gap: 8px;
  font-size: 14px;
  color: #555;
}

.like-btn,
.reply-btn {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
}

.like-btn.liked {
  color: red; /* 좋아요된 버튼은 빨간색으로 표시 */
}

.like-btn:hover,
.reply-btn:hover {
  text-decoration: underline;
}
.dropdown-container {
  position: relative;
  margin-left: auto;
}

.dots-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  padding: 0;
  margin-right: 8px; 
}

.dropdown-menu {
  position: absolute;
  top: 20px;
  right: 0;
  background: white;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.dropdown-item {
  display: inline-block; /* 콘텐츠 크기에 맞게 설정 */
  padding: 5px 10px; /* 버튼 내부 여백 */
  text-align: center; /* 텍스트 정렬 */
  background: none;
  cursor: pointer;
  white-space: nowrap; /* 텍스트가 줄바꿈되지 않도록 설정 */
}

.dropdown-item:hover {
  background: #f5f5f5;
}

.select_option {
  max-width: 150px; 
  font-size: 14px; 
  margin-top: 30px;
}

.my-select .v-input__slot {
  padding: 0 !important;
}
.content_deleted {
  padding-left: 20px;
  font-style: italic;
  color: #888;
}

.stars-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

.load-more-btn {
  color: #555;
  border: none;
  padding: 8px 16px;
  margin-top: 8px;
  cursor: pointer;
  border-radius: 4px;
  width: 100%;
}

.load-more-btn:hover {
  color: #666;
}
</style>