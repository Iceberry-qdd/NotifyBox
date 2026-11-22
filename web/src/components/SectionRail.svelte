<script lang="ts">
  export let activeSection = 0
  export let language: 'zh' | 'en' = 'zh'
  export let onNavigate: (index: number) => void = () => undefined

  const sections = [0, 1, 2, 3, 4]
</script>

<div class="progress-rail" aria-label="Section navigation">
  {#each sections as index}
    <button class:active={activeSection === index} onclick={() => onNavigate(index)} aria-label={`Go to section ${index + 1}`}><span></span></button>
  {/each}
  <button class="back-top" onclick={() => onNavigate(0)} aria-label={language === 'zh' ? '回到顶部' : 'Back to top'}><span>↑</span></button>
</div>

<style>
  .progress-rail {
    position: absolute;
    right: clamp(1.125rem, 3vw, 2.875rem);
    top: 50%;
    transform: translateY(-50%);
    z-index: 20;
    display: flex;
    flex-direction: column;
    gap: 0.5625rem;
  }

  .progress-rail button {
    width: 1.125rem;
    height: 1.125rem;
    display: grid;
    place-items: center;
    background: transparent;
  }

  .progress-rail button:not(.back-top) span {
    width: 0.25rem;
    height: 0.25rem;
    border-radius: 50%;
    background: var(--faint);
    transition: height .3s ease, background .3s ease, border-radius .3s ease;
  }

  .progress-rail button.active:not(.back-top) span {
    height: 1.25rem;
    border-radius: 0.125rem;
    background: var(--accent);
  }

  .progress-rail .back-top {
    margin-top: 0.1875rem;
    border: 0;
    padding: 0;
    width: 1.125rem;
    height: 1.125rem;
    color: var(--accent);
    font-size: 1.0625rem;
    line-height: 1;
  }

  .progress-rail .back-top span {
    width: auto;
    height: auto;
    background: transparent;
    border-radius: 0;
    color: var(--accent);
  }

  @media (max-width: 640px) {
    .progress-rail { right: 0.625rem; }
  }
</style>
