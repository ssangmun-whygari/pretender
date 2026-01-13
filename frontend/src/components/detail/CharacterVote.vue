<template>
  <v-sheet border class="mt-8 mb-3 rounded-lg sheet-container">
    <div class="slide-btn clickable" @click="$emit('hideCharacterView')">
      <v-sheet border class="w-100 h-100 d-flex justify-center align-center" :elevation="3">
        <v-icon icon="mdi-arrow-down-drop-circle" size="large"></v-icon>
      </v-sheet>
    </div>

    <h2>캐릭터 통계</h2>
    <!-- <LikeGraphPanel :media_id="id" :media_type="media_type" :cast_info = "castInfo"/> --> <!--deprecated-->
    <LikeGraphPanelV2 :cast_info = "castInfo" :vote="voteRef"/>
    <h2>캐릭터들</h2>
    <div v-if="totalItemLength > 0">
      <v-row v-for="n in Math.ceil(totalItemLength / denominator)" v-if="castInfo.length > 0">
        <v-col v-for="(item, index) in slicedCastInfo(castInfo, n - 1)" md="4" cols="12">
          <v-card class="cast-info-container pa-3 border rounded-lg" :hover="true" @click="expandPanel(n, index)">
            <v-row>
              <v-col class="d-flex align-center" cols="5">
                <img class="cast-image" :src="getProfileImagePath(item.profile_path)"></img>
              </v-col>
              <v-col cols="7">
                <!-- {{ `${n}, ${index}` }} --><v-card-title>{{ item.character }}</v-card-title>
                <v-card-subtitle>{{`(배우 : ${item.name})`}}</v-card-subtitle>
              </v-col>
            </v-row>
          </v-card>
        </v-col>

        <v-col v-show="panelExpanded[n - 1] === true" cols="12" v-if="true">
          <!-- {{ n - 1 }} -->
          <v-sheet :elevation="2" cols="12" class="pa-3 border rounded-lg">
            <v-row>
              <v-col v-if="smAndUp" cols="3">
                <img class="vote-target-profile-image" :src="getProfileImagePath(castInfo[currentActiveIndex].profile_path)"></img>
              </v-col>
              <v-col cols="9">
                <div v-if="likePerCharacterGraphPanelShown === false" class="d-flex flex-column h-100">
                  <div class="vote-panel-line1"><h2>당신이 가장 마음에 드는 캐릭터는?</h2></div>
                  <div class="vote-panel-line2">
                    <v-row>
                      <v-col cols="10">
                        <h1>{{ castInfo[currentActiveIndex].character }}</h1>
                        <div>{{ `(배우 : ${castInfo[currentActiveIndex].name})` }}</div>
                      </v-col>
                      <v-col cols="2"></v-col>
                    </v-row>
                  </div>
                  <div class="vote-panel-line3">
                    <v-row>
                      <v-col cols="10">
                        <div v-if="characterLikeCategory.length > 0">
                          <v-chip v-for="(item, _) in characterLikeCategory" 
                            :key="item.why"
                            :variant="getWhyButtonStyle(item.why)"
                            @click="setCharacterLikeWhy(item.why)"
                            class="mr-1"
                          >
                            {{ `#${item.content}` }}
                          </v-chip>
                        </div>
                      </v-col>
                      <v-col cols="2">
                        <v-btn v-if="votedByLoginedMember" color="secondary" @click="emit('update:voteChartDialogProxy', {show: true, character_id: character_id})">결과보기</v-btn>
                        <v-btn v-else color="primary" @click="vote(currentCharacterLikeWhy)">투표하기</v-btn>
                      </v-col>
                    </v-row>
                  </div>
                </div>

                <div v-if="likePerCharacterGraphPanelShown === true">
                  <LikePerCharacterGraphPanel
                    :media_id="id"
                    :media_type="type"
                    :character_id="character_id"
                  />
                </div>
              </v-col>
            </v-row>
          </v-sheet>
        </v-col>
      </v-row>
    </div>
    <div v-else>
      <h3>등록된 캐릭터 정보가 없습니다.</h3>
    </div>
  </v-sheet>
</template>

<style scoped>
  .container {
    container-type: inline-size;
  }

  a:link {
    width: 0;
    height: 0;
    margin: 0;
    padding: 0;
  }
  a:visited {
    width: 0;
    height: 0;
    margin: 0;
    padding: 0;
  }

  div {
    font-family: 'Pretendard-Regular', sans-serif;
  }

  .cast-image {
    max-height: 176px;
    /* aspect-ratio: 0.666 / 1; */
  }

  .cast-info-container {
    width: 100%;
    height: 200px;
  }

  .vote-panel-line3 {
    margin-top: auto;
  }

  .vote-target-profile-image {
    /* aspect-ratio: 0.666 / 1; */
    width: 100%;
    height: 250px;
    display: block;
    object-fit: contain;
    object-position: center;
  }

  .sheet-container {
    padding: 32px 12px 12px 12px;
    position: relative;
  }

  .slide-btn {
    position: absolute;
    top: 0;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 20%;
    height: 40px;
  }

  .clickable {
    cursor: pointer;
    user-select: none;
  }
</style>

<script setup>
  import axios from 'axios'
  import { ref, watch, toRef, computed, onMounted } from 'vue'
  import { useDisplay } from 'vuetify'
  import LikePerCharacterGraphPanel from './LikePerCharacterGraphPanel.vue'
  import LikeGraphPanel from './LikeGraphPanel.vue'
  import LikeGraphPanelV2 from './LikeGraphPanelV2.vue'

  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL;
  const profileBaseUrl = "http://image.tmdb.org/t/p/w185"
  const props = defineProps({
    id: String,
    type: String,
  })
  const { mdAndUp, smAndUp } = useDisplay()
  const emit = defineEmits(["requestOpenLoginModal", "update:voteChartDialogProxy", "update:mostVotedCharacter"]);
  const id = toRef(props, 'id')
  const type = toRef(props, 'type')
  const character_id = ref("")
  const currentCharacterLikeWhy = ref(-1)
  const totalItemLength = ref(0)
  const panelExpanded = ref([])
  const denominator = computed(() => mdAndUp.value ? 3 : 1)
  const currentActiveIndex = ref(0)
  const castInfo = ref([])
  const characterLikeCategory = ref([])
  const likePerCharacterGraphPanelShown = ref(false)
  let loginedId = null;
  const votedByLoginedMember = ref(false);
  const voteRef = ref({
    likeGraphPanelReady: false,
    apiSuccess: false,
    anyVoteExists: false,
    voteData: null
  });
  
  function getWhyButtonStyle(why) {
    if (votedByLoginedMember.value) {
      return 'tonal' // 이미 투표했으면 선택할 수 없음
    }
    return why === currentCharacterLikeWhy.value ? 'elevated' : 'tonal'
  }
  function getProfileImagePath(path) {
    if (path) return profileBaseUrl + path
    else return apiBaseUrl + '/resource/characterVoteNoImage'
  }
  let slicedCastInfo = (array, lineNum) => {
    // lineNum은 0부터 시작
    if (mdAndUp.value) return array.slice(lineNum * 3, Math.min(totalItemLength.value, (lineNum * 3) + 3))
    else return [array[lineNum]]
  }
  function expandPanel(lineNum, index) {
    if (mdAndUp.value) { // md이상
      console.log("######md이상")
      console.log(`######lineNum : ${lineNum}, index : ${index}`)
      index = (lineNum - 1) * 3 + index
    } else { // md이하
      console.log("######md이하")
      console.log(`######lineNum : ${lineNum}, index : ${index}`)
      index = lineNum - 1
    }
    currentActiveIndex.value = index
    likePerCharacterGraphPanelShown.value = false
    character_id.value = (castInfo.value)[index].id
    currentCharacterLikeWhy.value = -1
    panelExpanded.value = new Array(panelExpanded.value.length).fill(false);
    (panelExpanded.value)[lineNum - 1] = true
  }
  function showLikePerCharacterGraphPanel() {
    likePerCharacterGraphPanelShown.value = true;
  }
  function setCharacterLikeWhy(why) {
    if (votedByLoginedMember.value) {
      return currentCharacterLikeWhy.value = -1;
    }
    currentCharacterLikeWhy.value = why;
  }

  async function vote(why) {
    let loginedId = await checkAuthenticated()
    if (!loginedId) {
      // 로그인 안했다면 로그인 요청하고 조기 종료
      emit('requestOpenLoginModal')
      return;
    }
    // alert(`투표합니다. why : ${why}`);
    if (characterLikeCategory.value.map(entry => entry.why).indexOf(why) === -1) {
      // alert('유효하지 않은 투표 이유')
      return
    }
    const payload = {
      memberId: loginedId,
      mediaId: id.value,
      characterId: character_id.value,
      type: type.value,
      why: why,
    };
    alert(JSON.stringify(payload));
    const response = await axios.post(apiBaseUrl + "/api/detail/vote", payload);
    console.log(response.data)
    if (response.data.result === "success") {
      // alert("투표 성공");
      votedByLoginedMember.value = true;
      await updateVoteData(voteRef); // 투표 결과를 갱신함.
    } else if (response.data.result === "duplicated") {
      // alert("이미 투표함");
      votedByLoginedMember.value = true;
    }
  }

  async function checkAuthenticated() {
    loginedId = null;
    try {
      let response = await axios.get(
        apiBaseUrl + '/api/authenticated',
        {
          withCredentials: true,

          headers: {
            "X-Requested-With": "XMLHttpRequest"
          }
        }
      ) // axios.get end
      if (response.data.authenticated) {
        // TODO : loginedId 변경
        return response.data.id;
      }
    } catch (error) {
      console.log(error);
      return null;
    }
    return null;
  }

  async function requestCharacterLikeCategory() {
    let response = await axios.get(
      apiBaseUrl + '/api/detail/cast/likeCategory',
    ) // axios.get end
    characterLikeCategory.value = response.data
  }

  async function requestCastInfo() {
    let response = await axios.get(
      apiBaseUrl + '/api/detail/cast',
      {
        params : {
          type: type.value,
          mediaId: id.value,
        }
      }
    ) // axios.get end
    console.log("########## requestCastInfo() ##########")
    console.log(response)
    // TODO : 애니메이션하고 일반 tv 시리즈하고 다른 처리가 필요함
    castInfo.value = response.data.cast
    totalItemLength.value = response.data.cast.length
    panelExpanded.value = new Array(totalItemLength.value).fill(false);
    console.log(castInfo.value)
    console.log("########## requestCastInfo() END ##########")
  }

  async function getVoteByCurrentMember(loginedId) {
    try {
      let response = await axios.get(
        apiBaseUrl + '/api/detail/vote',
        {
          params: {
            type: type.value,
            mediaId: id.value,
            memberId: loginedId
          }
        }
      )
      console.log("########### getVoteByCurrentMember() ############");
      console.log(response)
      console.log("########### getVoteByCurrentMember() END ############");
      return response.data.result
    } catch (error) {
      console.error("API 호출 실패", error);
      return null;
    }
  }

  async function fetchVoteData(media_id, media_type) {
    try {
      const response = await axios.get(
        apiBaseUrl + '/api/detail/castVotes', // url
        {
          params: {
            mediaId: media_id, 
            type: media_type
          }
        }
      )
      return response
    } catch (error) {
      console.error("투표 정보를 불러오는 데 실패했습니다.")
      console.error(error)
      return null
    }
  }

  async function updateVoteData(voteRef) {
    let temp = {
      likeGraphPanelReady: false,
      apiSuccess: false,
      anyVoteExists: false,
      voteData: null
    }

    const response = await fetchVoteData(props.id, props.type);
    if (!response) {
      temp.likeGraphPanelReady = true;
      voteRef.value = temp;
      return;
    }
    temp.apiSuccess = true;
    if (response.data.length <= 0) {
      temp.likeGraphPanelReady = true;
      temp.voteData = response.data;
      voteRef.value = temp;
      return;
    }
    temp.anyVoteExists = true;
    temp.voteData = response.data;
    temp.likeGraphPanelReady = true;
    voteRef.value = temp;
  }

  function updateMostVotedCharacterInfo(obj) {
    emit("update:mostVotedCharacter", obj);
  }

  watch(() => {return { vote: voteRef.value, castInfo: castInfo.value }}, (newVal, oldVal) => {
    console.log("newVal", newVal)
    if (newVal.vote.voteData && newVal.vote.voteData.length == 0) {
      return updateMostVotedCharacterInfo({apiFetched: true, result: null})
    }
    if (newVal.vote.voteData && newVal.vote.voteData.length > 0 && newVal.castInfo.length > 0) {
      let voteData = newVal.vote.voteData;
      let castInfo = newVal.castInfo;
      let i = castInfo.findIndex((c) => c.id === voteData[0].character_id);
      updateMostVotedCharacterInfo({
        apiFetched: true,
        result: {
          characterName : (i === -1) ? 'Unknown' : castInfo[i].character,
          actorName: (i === -1) ? 'Unknown' : castInfo[i].name,
          profilePath: (i === -1) ? '' : castInfo[i].profile_path
        }
      });
    }
  })

  onMounted(async () => {
    loginedId = await checkAuthenticated();
    if (loginedId) {
      let exists = await getVoteByCurrentMember(loginedId);
      votedByLoginedMember.value = exists ? true : false;
    }
    await updateVoteData(voteRef);
    await requestCastInfo();
    await requestCharacterLikeCategory();
  })
</script>