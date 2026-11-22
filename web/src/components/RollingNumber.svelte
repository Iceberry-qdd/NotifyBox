<script lang="ts">
  export let value = ''
  export let active = false

  const reelDigits = Array.from({ length: 30 }, (_, index) => index % 10)
  $: characters = [...value]
</script>

<span class="rolling-number" class:is-rolling={active} aria-label={value}>
  {#each characters as character, index}
    {#if /\d/.test(character)}
      <span class="digit-window" style={`--digit-stop:${20 + Number(character)}; --digit-delay:${index * 90}ms`} aria-hidden="true">
        <span class="digit-strip">
          {#each reelDigits as digit}
            <span>{digit}</span>
          {/each}
        </span>
      </span>
    {:else}
      <span class="number-suffix" aria-hidden="true">{character}</span>
    {/if}
  {/each}
</span>

<style>
  .rolling-number {
    display: inline-flex;
    align-items: baseline;
    font-variant-numeric: tabular-nums;
    letter-spacing: normal;
  }

  .digit-window {
    display: inline-block;
    width: 0.72em;
    min-width: 0.72em;
    height: 1em;
    overflow: hidden;
    line-height: 1;
    vertical-align: bottom;
  }

  .digit-strip {
    display: flex;
    flex-direction: column;
    width: 100%;
    transform: translateY(calc(var(--digit-stop) * -1em));
  }

  .digit-strip > span {
    display: block;
    width: 100%;
    height: 1em;
    line-height: 1em;
    text-align: center;
  }

  .rolling-number.is-rolling .digit-strip {
    animation: reel-spin .9s cubic-bezier(.2, .8, .2, 1) var(--digit-delay) both;
  }

  @keyframes reel-spin {
    from { transform: translateY(0); }
    to { transform: translateY(calc(var(--digit-stop) * -1em)); }
  }
</style>
