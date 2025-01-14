<template>
  <div>
    <h2 v-if="isLoading">로딩 중...</h2>
    <h2 v-else-if="error">{{ error }}</h2>
    <div v-else>
      <h2>댓글 {{ totalComments }}개</h2>
          <div class="sort-container">
            <label for="sort">정렬:</label>
            <select id="sort" v-model="sortOrder" @change="updateComments">
              <option value="no">최신순</option>
              <option value="likeCount">좋아요순</option>
            </select>
          </div>
      <ul class="comment-list">
        <li v-for="comment in comments" :key="comment.no" class="comment-item">
          <img :src="comment.image" alt="프로필" class="comment-image" />
          <div class="comment-content">
            <div class="comment-header">
              <span class="nickname">{{ comment.nickname }}</span>
              <span class="time">{{ formatDate(comment.post_date) }}</span>
              <span v-if="comment.correct_date">
                (수정됨: {{ formatDate(comment.correct_date) }})
              </span>
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
              <p class="content">
                {{ comment.content }}
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
            <li v-for="reply in replies[comment.no]" :key="reply.no" class="comment-item">
              <img :src="reply.image" alt="프로필" class="comment-image" />
              <div class="comment-content">
                <div class="comment-header">
                  <span class="nickname">{{ reply.nickname }}</span>
                  <span class="time">{{ formatDate(reply.post_date) }}</span>
                    <span v-if="reply.correct_date">
                      (수정됨: {{ formatDate(reply.correct_date) }})
                    </span>
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
                    <p class="content">
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
    <!-- 대댓글 입력 창 -->
              <div class="reply-input-container">
                <textarea
                placeholder="답글을 입력하세요..."
                class="reply-textarea"
                v-model="newReply"
                @input="handleInput"
              ></textarea>
              <div class="reply-actions">
                <button
                  @click="cancelReply"
                  class="cancel-btn"
                  :class="{ active: newReply.trim() !== '' }"
                >
                  취소
                </button>
                <button
                  @click="submitReply(comment.no)"
                  class="submit-btn"
                  :disabled="!newReply.trim()"
                >
                 답글</button>
                </div>
              </div>
           </ul>


           
            
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import { useNavigationStore } from '../stores/navigation';

const comments = ref([]);
const replies = ref([]);
const newReply = ref(''); // newReply 변수 초기화
const likedCommentIds = ref([]); // 사용자가 좋아요한 댓글 ID 목록
const totalComments = ref(0); // 전체 댓글 수
const isLoading = ref(true); // 로딩 상태 관리
const error = ref(null); // 오류 메시지 관리
const activeDropdown = ref(null); // 활성화된 드롭다운 ID
const loggedInUserId = ref(null);

const route = useRoute();
const contentId = ref(route.query.id ||null);

const router = useRouter();
const navigationStore = useNavigationStore(); // Pinia 스토어 초기화
const sortOrder = ref("likeCount");

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
    loggedInUserId.value = response.data.userId; // 서버에서 반환된 로그인 사용자 ID
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
  if (item.members_Id === loggedInUserId.value) {
  activeDropdown.value = activeDropdown.value === item.no ? null : item.no;
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

      // UI에서 댓글 삭제
      comments.value = comments.value.filter((comment) => comment.no !== item.no);
      for (const parentId in replies.value) {
        replies.value[parentId] = replies.value[parentId].filter(
          (reply) => reply.no !== item.no
        );
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
  if (count == null || count === undefined) return "0"; 
  if (count >= 10000) return `${(count / 10000).toFixed(1)}만`;
  return count.toString();
};

// 댓글 가져오기 함수
const fetchComments = async (contentId, sortBy = "likeCount") => {
  try {
    const response = await axios.get(`http://localhost:8080/api/comments`, {
      params: { id: contentId, sortBy }, // id와 sortBy를 쿼리 파라미터로 전달
    });

    if (response.data && response.data.comments) {
      comments.value = response.data.comments; // 댓글 목록 저장
      totalComments.value = response.data.comments[0]?.count || 0; // 전체 댓글 수 저장
    }
  } catch (err) {
    console.error("API 호출 중 오류가 발생했습니다:", err);
    error.value = "댓글 정보를 불러오는 데 실패했습니다.";
  } finally {
    isLoading.value = false; // 로딩 상태 해제
  }
};


// 정렬 옵션 변경 시 호출되는 함수
const updateComments = () => {
  isLoading.value = true; // 로딩 상태 설정
  fetchComments(contentId.value, sortOrder.value); // sortOrder를 기반으로 fetchComments 호출
};



// 대댓글 가져오기 함수
const fetchReplies = async (parent_no) => {
  try {
    const response = await axios.post('http://localhost:8080/api/replies', null, {
      params: { id: contentId.value, page: 0, parentId: parent_no },
    });
    if (response.data && response.data.replies) {
      replies.value[parent_no] = response.data.replies;
    }
  } catch (err) {
    console.error('대댓글 정보를 불러오는 데 실패했습니다:', err);
    error.value = '대댓글 정보를 불러오는 데 실패했습니다.';
  }
};

// 대댓글 토글 함수
const toggleReplies = async (comment) => {
  const parent_no = comment.no;
  if (replies.value[parent_no]) {
    delete replies.value[parent_no];
  } else {
    await fetchReplies(parent_no);
  }
};

// 대댓글 입력 취소
const cancelReply = () => {
  const replyTextarea = document.querySelector('.reply-textarea');
   newReply.value = '';
};

// 대댓글 제출
const submitReply = async (parent_no) => {
  const isAuthenticated = await checkAuthenticated(); // 로그인 상태 확인

  if (!isAuthenticated) {
    // 로그인되지 않은 경우
    navigationStore.setPreviousPage(router.currentRoute.value.fullPath); // 현재 페이지 저장
    router.push('/login'); // 로그인 페이지로 이동
    return;
  }

  if (!newReply.value.trim()) {
    console.error('대댓글 내용을 입력해주세요.');
    return;
  }

  try {
    await axios.post('http://localhost:8080/api/insertReview', {
      parent_no,
      content: newReply.value,
    },{
      params:{ id: contentId.value },
      withCredentials: true, // 인증 정보를 포함하도록 설정
    });
    newReply.value = '';
    if(parent_no == 0){
      await fetchComments(contentId.value,sortOrder.value);
    }else{
      await fetchReplies(parent_no); 
    }

  } catch (err) {
    if (err.response) {
    console.error('대댓글 추가 중 오류가 발생했습니다:', err.response.data);
  } else {
    console.error('대댓글 추가 중 알 수 없는 오류가 발생했습니다:', err.message);
  }
  }
};

// 컴포넌트 마운트 시 API 호출
onMounted(async () => {

  console.log(`contentId: ${contentId.value}`);
    // 이전 페이지 설정
    const currentPath = router.currentRoute.value.fullPath;
  console.log('현재 페이지:', currentPath);

  if (!navigationStore.getPreviousPage() || navigationStore.getPreviousPage() === '/') {
    navigationStore.setPreviousPage(currentPath); // 현재 페이지를 이전 페이지로 설정
    console.log('리다이렉트할 이전 페이지 설정:', currentPath);
  }

  if (contentId) {
    await fetchLikedCommentIds(contentId.value); // 좋아요한 댓글 ID 가져오기
    await fetchComments(contentId.value,sortOrder.value); // 댓글 불러오기
  } else {
    console.error('contentId가 없습니다.');
  }

  await fetchLoggedInUserId();
 
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
  gap: 16px; /* 닉네임과 시간 사이의 간격 */
  position: relative;
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

.sort-container {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-container label {
  font-size: 14px;
  font-weight: bold;
}

.sort-container select {
  padding: 5px;
  font-size: 14px;
}

</style>