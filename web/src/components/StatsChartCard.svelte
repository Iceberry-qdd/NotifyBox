<script lang="ts">
  export let type: 'donut' | 'bars' = 'donut'
  export let active = false

  const barHeights = [42, 68, 54, 82, 61, 91, 48, 74, 96, 57, 78, 64]
</script>

<div class={`stats-chart-card stats-chart-card-${type}`} class:is-active={active} aria-hidden="true">
  {#if type === 'donut'}
    <div class="donut-chart"></div>
  {:else}
    <div class="bar-chart">
      {#each barHeights as height, index}
        <span style={`--bar-height:${height}%; --bar-delay:${index * 65}ms`}><i></i></span>
      {/each}
    </div>
  {/if}
</div>

<style>
  .stats-chart-card {
    position: absolute;
    display: grid;
    place-items: center;
    background: color-mix(in srgb, var(--surface-solid) 91%, transparent);
    box-shadow: var(--shadow);
    z-index: 3;
  }

  .stats-chart-card-donut {
    width: clamp(9.5rem, 76%, 12rem);
    aspect-ratio: 1.08;
    padding: 1.125rem;
    left: -6.25rem;
    top: 23%;
    border-radius: 1.25rem;
    transform: rotate(-9deg);
  }

  .stats-chart-card-bars {
    width: clamp(12rem, 92%, 15rem);
    aspect-ratio: 1.65;
    padding: 1.125rem;
    right: -8.25rem;
    bottom: 32%;
    border-radius: 1.25rem;
    transform: rotate(8deg);
    overflow: hidden;
  }

  .donut-chart {
    position: relative;
    --donut-angle: 0deg;
    width: min(8.75rem, 100%);
    aspect-ratio: 1;
    border-radius: 50%;
    background: conic-gradient(var(--accent) 0 34%, #77c9a0 34% 57%, #fe9900 57% 81%, #df4a3f 81% 100%);
    mask: conic-gradient(#000 0 var(--donut-angle), transparent var(--donut-angle) 360deg);
    -webkit-mask: conic-gradient(#000 0 var(--donut-angle), transparent var(--donut-angle) 360deg);
    opacity: 1;
    transform: none;
    animation: none;
  }

  .stats-chart-card.is-active .donut-chart {
    animation: donut-fill 1.1s cubic-bezier(.2, .8, .2, 1) forwards;
  }

  .donut-chart::before {
    content: '';
    position: absolute;
    inset: 1.1875rem;
    border-radius: 50%;
    background: var(--surface-solid);
  }

  .bar-chart {
    width: 100%;
    height: 100%;
    min-width: 0;
    min-height: 0;
    display: flex;
    align-items: end;
    justify-content: space-around;
    gap: 0.25rem;
    overflow: hidden;
  }

  .bar-chart > span {
    flex: 1 1 0;
    min-width: 0;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: end;
    align-items: center;
  }

  .bar-chart i {
    display: block;
    width: 70%;
    max-width: 1.25rem;
    height: var(--bar-height);
    border-radius: 0.1875rem 0.1875rem 0.0625rem 0.0625rem;
    background: var(--accent);
    transform-origin: bottom;
    transform: scaleY(0);
  }

  .stats-chart-card.is-active .bar-chart i {
    animation: bar-in .55s cubic-bezier(.2, .85, .25, 1) var(--bar-delay) forwards;
  }

  @property --donut-angle {
    syntax: '<angle>';
    inherits: false;
    initial-value: 0deg;
  }

  @keyframes donut-fill {
    to { --donut-angle: 360deg; }
  }

  @keyframes bar-in {
    to { transform: scaleY(1); }
  }

  @media (max-width: 640px) {
    .stats-chart-card-donut {
      width: 9.5rem;
      padding: 0.875rem;
      left: -5.25rem;
      top: 22%;
    }

    .stats-chart-card-bars {
      width: 12rem;
      padding: 0.875rem;
      right: -6.75rem;
      bottom: 22%;
    }

    .donut-chart::before { inset: 0.9375rem; }
    .bar-chart { gap: 0.3125rem; }
    .bar-chart i { width: 0.625rem; }
  }
</style>
