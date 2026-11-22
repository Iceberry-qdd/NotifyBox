<script lang="ts">
    import { onMount } from "svelte";
    import androidIcon from "../assets/android.svg";

    export let label = "";
    export let androidOnlyLabel = "";
    export let meta = "";
    let isAppleDevice = false;

    type UserAgentData = {
        platform?: string;
        getHighEntropyValues?: (hints: string[]) => Promise<{ platform?: string }>;
    };

    async function detectAppleDevice() {
        const navigatorWithHints = navigator as Navigator & { userAgentData?: UserAgentData };
        const userAgentData = navigatorWithHints.userAgentData;
        let platform = userAgentData?.platform ?? "";

        if (userAgentData?.getHighEntropyValues) {
            try {
                platform = (await userAgentData.getHighEntropyValues(["platform"])).platform ?? platform;
            } catch {
                // Keep the low-entropy platform value as a fallback.
            }
        }

        const normalizedPlatform = platform.toLowerCase();
        const isApplePlatform = /mac|ios|iphone|ipad|ipod/.test(normalizedPlatform);
        const isDesktopModeIpad = normalizedPlatform.includes("mac") && navigator.maxTouchPoints > 1;

        if (userAgentData) return isApplePlatform || isDesktopModeIpad;

        return /macintosh|iphone|ipad|ipod/.test(`${navigator.platform} ${navigator.userAgent}`.toLowerCase());
    }

    function blockAppleDownload(event: MouseEvent) {
        if (isAppleDevice) event.preventDefault();
    }

    onMount(() => {
        void detectAppleDevice().then((detected) => (isAppleDevice = detected));
    });
</script>

<a
    class="download-button primary-button"
    class:is-disabled={isAppleDevice}
    href={isAppleDevice ? undefined : "https://github.com/Iceberry-qdd/NotifyBox/releases/latest/download/NotifyBox.apk"}
    download={isAppleDevice ? undefined : true}
    aria-disabled={isAppleDevice}
    tabindex={isAppleDevice ? -1 : 0}
    onclick={blockAppleDownload}>
    <img
        src={androidIcon}
        alt="" />
    <span class="button-copy">
        <strong>{isAppleDevice ? androidOnlyLabel : label}</strong>
        <small>{meta}</small>
    </span>
    <span
        class="material-symbols-outlined button-action"
        aria-hidden="true">download</span>
</a>

<style>
    .primary-button {
        display: inline-flex;
        align-items: center;
        gap: 0.75rem;
        min-height: 3rem;
        padding: 0.625rem 1rem 0.625rem 0.875rem;
        border-radius: 999rem;
        background: var(--accent);
        color: #fff;
        transition:
            transform 0.2s ease,
            background 0.2s ease;
    }
    .primary-button:hover {
        transform: translateY(-0.1875rem);
    }
    .primary-button > img {
        width: 1.125rem;
        height: 1.125rem;
        flex: 0 0 auto;
        object-fit: contain;
    }
    .button-copy {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        gap: 0.125rem;
    }
    .button-copy strong {
        color: #fff;
        font-size: 0.75rem;
        line-height: 1;
    }
    .button-copy small {
        color: rgb(255 255 255 / 82%);
        font-family: var(--mono);
        font-size: 0.5625rem;
        line-height: 1;
    }
    .button-action {
        margin-left: auto;
        color: #fff;
        font-size: 1rem;
        font-weight: 400;
    }
    .primary-button.is-disabled {
        cursor: not-allowed;
        filter: saturate(0.45);
    }

    @media (max-width: 640px) {
        .primary-button {
            width: 80%;
        }
    }
</style>
