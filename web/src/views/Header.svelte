<script lang="ts">
    export let appName = "";
    export let language: "zh" | "en" = "zh";
    export let theme: "light" | "dark" = "light";
    export let isScrolled = false;
    export let onHome: () => void = () => undefined;
    export let onLanguage: () => void = () => undefined;
    export let onTheme: (event: MouseEvent) => void = () => undefined;
</script>

<header
    class="topbar"
    class:scrolled={isScrolled}>
    <button
        class="brand"
        onclick={onHome}
        aria-label={appName}>
        <img
            src={`${import.meta.env.BASE_URL}icon.webp`}
            alt="" />
        <span>{appName}<span class="brand-dot">.</span></span>
    </button>
    <div class="topbar-actions">
        <button
            class="text-control"
            onclick={onLanguage}
            aria-label={language === "zh" ? "当前语言：中文" : "Current language: English"}>
            {language === "zh" ? "中" : "EN"}
        </button>
        <button
            class="theme-control"
            onclick={onTheme}
            aria-label={theme === "dark" ? "当前主题：深色" : "Current theme: Light"}>
            <span
                class="material-symbols-outlined"
                aria-hidden="true">
                {theme === "dark" ? "dark_mode" : "light_mode"}
            </span>
        </button>
    </div>
</header>

<style>
    .topbar {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 10%;
        padding: 1.375rem clamp(1.5rem, 5vw, 5.25rem);
        display: flex;
        align-items: center;
        justify-content: space-between;
        z-index: 20;
        pointer-events: none;
        transition:
            background 0.3s ease,
            border-color 0.3s ease,
            backdrop-filter 0.3s ease;
        border-bottom: 0.0625rem solid transparent;
    }

    .topbar.scrolled {
        background: color-mix(in srgb, var(--bg) 67%, transparent);
        border-bottom-color: var(--line);
        backdrop-filter: blur(0.625rem) saturate(3.5);
        -webkit-backdrop-filter: blur(0.625rem) saturate(3.5);
    }

    .topbar > * {
        pointer-events: auto;
    }

    .brand {
        display: inline-flex;
        align-items: center;
        gap: 0.625rem;
        padding: 0;
        background: transparent;
        font-size: 0.875rem;
        font-weight: 800;
        letter-spacing: -0.03em;
    }

    .brand img {
        width: 1.75rem;
        height: 1.75rem;
        border-radius: 0.5rem;
        box-shadow: 0 0 0 0.0625rem var(--line);
    }

    .brand-dot {
        color: var(--accent);
    }

    .topbar-actions {
        display: flex;
        align-items: center;
        gap: 0.5625rem;
    }

    .text-control,
    .theme-control {
        min-width: 1.75rem;
        height: 1.75rem;
        display: grid;
        place-items: center;
        border-radius: 50%;
        background: transparent;
        color: var(--muted);
        transition:
            background 0.25s ease,
            color 0.25s ease;
    }

    .text-control {
        font-size: 0.6875rem;
        font-weight: 700;
    }

    .text-control:hover,
    .theme-control:hover {
        background: var(--accent-soft);
        color: var(--text);
    }

    @media (max-width: 640px) {
        .topbar {
            height: 4.125rem;
            padding: 1.125rem 1.25rem;
        }

        .brand {
            font-size: 0.8125rem;
        }

        .brand img {
            width: 1.5625rem;
            height: 1.5625rem;
        }
    }
</style>
