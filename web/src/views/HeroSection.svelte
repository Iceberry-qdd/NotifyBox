<script lang="ts">
    import Phone from "../components/Phone.svelte";
    import NotificationCard from "../components/NotificationCard.svelte";
    import DownloadButton from "../components/DownloadButton.svelte";
    import ProjectButton from "../components/ProjectButton.svelte";
    import wechatIcon from "../assets/wechat.svg";
    import spotifyIcon from "../assets/spotify.svg";
    import xiaohongshuIcon from "../assets/xiao-hong-shu.svg";
    import { screenshot } from "../lib/screenshots";
    import { tokens } from "../lib/text";

    export let t: any;
    export let language: "zh" | "en" = "zh";
    export let theme: "light" | "dark" = "light";
    export let active = false;

    const notificationCopy = {
        zh: [
            { app: "微信", title: "收到一条新消息", body: "今晚 8:00 一起吃饭吗？", time: "刚刚", icon: wechatIcon },
            { app: "Spotify", title: "正在播放", body: "Daily Mix 1 · 为你推荐的音乐", time: "1 分钟前", icon: spotifyIcon },
            {
                app: "小红书",
                title: "有人收藏了你的笔记",
                body: "你的生活记录获得了新的回应",
                time: "3 分钟前",
                icon: xiaohongshuIcon,
            },
        ],
        en: [
            { app: "WeChat", title: "New message received", body: "Dinner at 8 tonight?", time: "now", icon: wechatIcon },
            {
                app: "Spotify",
                title: "Now playing",
                body: "Daily Mix 1 · picked for you",
                time: "1 min ago",
                icon: spotifyIcon,
            },
            {
                app: "Xiaohongshu",
                title: "Your note was saved",
                body: "Someone saved a moment from your day",
                time: "3 min ago",
                icon: xiaohongshuIcon,
            },
        ],
    } as const;

    $: notificationItems = notificationCopy[language];
</script>

<section
    class="snap-section hero-section"
    class:section-active={active}
    id="collect"
    data-section="0">
    <div class="ambient ambient-one"></div>
    <div class="section-grid hero-grid">
        <div class="copy-block hero-copy">
            <p class="eyebrow"><span class="eyebrow-line"></span>{t.eyebrow}</p>
            <h1>
                {#each tokens(t.heroTitle, language) as token, index}
                    <span
                        class="reveal-word"
                        style={`--word-delay:${index * 32}ms`}>{token}</span>
                {/each}
            </h1>
            <p class="lede">
                {#each tokens(t.heroBody, language) as token, index}
                    <span
                        class="reveal-word"
                        style={`--word-delay:${index * 24}ms`}>
                        {token}
                    </span>
                {/each}
            </p>
            <div class="hero-actions">
                <DownloadButton
                    label={t.download}
                    androidOnlyLabel={t.androidOnly}
                    meta={t.downloadMeta} />
                <ProjectButton label={t.source} />
            </div>
        </div>

        <div
            class="hero-visual"
            aria-label="Animated notification collection preview">
            <div class="orbit orbit-one"></div>
            <div class="orbit orbit-two"></div>
            <div class="phone-hero">
                <Phone
                    variant="hero-device"
                    src={screenshot("home", language, theme)}
                    alt={`${t.appName} notification list screenshot`} />
                <div
                    class="notification-stack"
                    aria-label={language === "zh" ? "模拟通知弹出动画" : "Simulated notification pop-ups"}>
                    {#each notificationItems as notification, index}
                        <NotificationCard {notification} {index} />
                    {/each}
                </div>
            </div>
        </div>
    </div>
</section>

<style>
    .hero-section {
        min-height: 300svh;
        display: block;
        padding: 0;
        overflow: visible;
    }

    .hero-section .hero-grid {
        height: 100svh;
        min-height: 0;
        padding: 6.25rem 0 4.5rem;
        position: sticky;
        top: 0;
        grid-template-columns: minmax(18.75rem, 0.86fr) minmax(28.125rem, 1.14fr);
    }

    .hero-actions {
        display: flex;
        gap: 0.8125rem;
        align-items: center;
        margin-top: 2.125rem;
    }

    .hero-visual {
        min-height: 34.375rem;
        position: relative;
        display: grid;
        place-items: center;
    }

    .ambient-one {
        top: 14%;
        right: 7%;
        background: #4664d6;
        --ambient-x: 1.5rem;
        --ambient-y: 1rem;
    }

    .phone-hero {
        position: relative;
        width: clamp(14.6875rem, 23vw, 19.0625rem);
        height: auto;
        top: clamp(4.5rem, 8svh, 6rem);
        aspect-ratio: 1440 / 3200;
        overflow: visible;
        z-index: 2;
    }

    .hero-visual .orbit {
        border: 1px solid var(--line);
        border-radius: 50%;
        position: absolute;
        pointer-events: none;
        transform: rotate(-22deg);
    }

    .hero-visual .orbit-one {
        width: 28.75rem;
        height: 12.8125rem;
        transform: rotate(29deg);
    }

    .hero-visual .orbit-two {
        width: 31.875rem;
        height: 15.625rem;
        transform: rotate(-34deg);
        border-color: rgba(145, 169, 255, 0.35);
    }

    .notification-stack {
        position: absolute;
        z-index: 5;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        transform: rotate(5deg);
    }

    @media (max-width: 640px) {
        .hero-section {
            min-height: 300svh;
            margin-bottom: 50svh;
        }

        .hero-actions {
            flex-direction: column;
        }

        .hero-section .hero-grid {
            height: 100svh;
            min-height: 0;
            padding: 5.375rem 0 3.4375rem;
            grid-template-columns: 1fr;
        }

        .hero-section .copy-block {
            align-self: end;
        }

        .hero-visual {
            min-height: 23.125rem;
        }

        .phone-hero {
            width: 11.875rem;
            height: auto;
            top: clamp(3.75rem, 7svh, 4.75rem);
        }

        .notification-stack {
            transform: scale(0.9) rotate(5deg);
            transform-origin: top left;
        }
    }
</style>
