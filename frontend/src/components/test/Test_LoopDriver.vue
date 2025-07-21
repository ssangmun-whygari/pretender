<script setup>
import { useLoop, useTresContext } from '@tresjs/core'

const { renderer, scene, camera } = useTresContext();
// 각 캔버스 안에서 개별적으로 rAF 실행
useLoop().render(({ renderer, scene, camera }) => {
  renderer.render(scene, camera)
})
</script>