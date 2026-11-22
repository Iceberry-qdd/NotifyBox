<script lang="ts">
  import githubIcon from "../assets/github.svg";
  export let label = "";
</script>

<a class="secondary-button" href="https://github.com/Iceberry-qdd/NotifyBox" target="_blank" rel="noreferrer">
  <img class="github-icon" src={githubIcon} alt="" />
  <span>{label}</span>
  <span class="material-symbols-outlined button-action" aria-hidden="true">arrow_outward</span>
</a>

<style>
  .secondary-button {
    display: inline-flex;
    align-items: center;
    gap: 0.75rem;
    min-height: 3rem;
    padding: 0.75rem 1rem;
    border: 1px solid var(--line-bright);
    border-radius: 999rem;
    color: var(--muted);
    transition:
      color 0.2s ease,
      border-color 0.2s ease,
      transform 0.2s ease;
  }
  .secondary-button:hover {
    color: var(--text);
    border-color: var(--accent);
    transform: translateY(-0.1875rem);
  }
  .secondary-button > img {
    width: 1.125rem;
    height: 1.125rem;
    flex: 0 0 auto;
    object-fit: contain;
  }
  :root:not([data-theme="light"]) .secondary-button > img {
    filter: invert(1);
  }
  .secondary-button > span:not(.button-action) {
    font-size: 0.75rem;
    font-weight: 700;
    white-space: nowrap;
  }
  .button-action {
    margin-left: auto;
    font-size: 1rem;
  }

  @media (max-width: 640px) {
    .secondary-button {
      width: 80%;
    }
  }
</style>
