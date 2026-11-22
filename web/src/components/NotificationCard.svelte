<script lang="ts">
    export let notification: { app: string; title: string; body: string; time: string; icon: string };
    export let index = 0;
</script>

<article
    class={`notification-card notification-card-${index + 1}`}
    aria-label={`${notification.app}: ${notification.title}`}>
    <img
        class="notification-app-icon"
        src={notification.icon}
        alt="" />
    <div class="notification-content">
        <div class="notification-header">
            <span>{notification.app}</span>
            <time>{notification.time}</time>
        </div>
        <b>{notification.title}</b>
        <p>{notification.body}</p>
    </div>
</article>

<style>
    .notification-card {
        position: absolute;
        top: 0;
        left: 0;
        width: calc(100% - 1.4rem);
        min-height: 4.75rem;
        max-height: 100%;
        display: flex;
        align-items: flex-start;
        gap: 0.75rem;
        padding: 0.75rem;
        border: 1px solid var(--line-bright);
        background: color-mix(in srgb, var(--surface-solid) 93%, transparent);
        box-shadow: 0 0.15rem 1rem rgb(0 0 0 / 15%);
        border-radius: 1rem;
        --notification-stack-bg: #faf9fe;
        --notification-stack-title: var(--text);
        --notification-stack-muted: var(--muted);
        color: var(--notification-stack-muted);
        opacity: 0;
        transform: translate(0.7rem, calc(0.7rem + var(--notification-start-y))) scale(1) rotate(0deg);
        animation: notification-sequence linear both;
        animation-timeline: --page-scroll;
        animation-range: var(--notification-range-start, 0%) 52%;
    }

    .notification-card-1 {
        --notification-x: 51%;
        --notification-y: 200%;
        --notification-start-y: 0rem;
        --notification-stack-y: 0rem;
        --notification-range-start: 0%;
        --notification-rotate: -4deg;
        z-index: 3;
    }
    .notification-card-2 {
        --notification-x: -62%;
        --notification-y: 400%;
        --notification-start-y: 0.25rem;
        --notification-stack-y: 100%;
        --notification-range-start: 4%;
        --notification-rotate: 2deg;
        z-index: 2;
    }
    .notification-card-3 {
        --notification-x: 60%;
        --notification-y: 550%;
        --notification-start-y: 0.5rem;
        --notification-stack-y: 200%;
        --notification-range-start: 8%;
        --notification-rotate: -8deg;
        z-index: 1;
    }

    .notification-app-icon {
        width: 2rem;
        height: auto;
        flex: 0 0 auto;
        border-radius: 0.625rem;
        object-fit: cover;
    }
    .notification-content {
        min-width: 0;
        flex: 1;
    }
    .notification-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        color: var(--notification-stack-muted);
        font-size: 0.625rem;
        line-height: 1.2;
    }
    .notification-header span {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
    .notification-header time {
        margin-left: auto;
        white-space: nowrap;
    }
    .notification-card b {
        display: block;
        margin-top: 0.3rem;
        color: var(--notification-stack-title);
        font-size: 0.75rem;
        line-height: 1.25;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
    .notification-card p {
        margin: 0.2rem 0 0;
        color: var(--notification-stack-muted);
        font-size: 0.625rem;
        line-height: 1.35;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    @keyframes notification-sequence {
        0%,
        28% {
            opacity: 1;
            transform: translate(0.7rem, calc(0.7rem + var(--notification-start-y))) scale(1) rotate(0deg);
        }
        36%,
        52% {
            opacity: 1;
            transform: translate(var(--notification-x), var(--notification-y)) scale(1) rotate(var(--notification-rotate));
            background: var(--surface-solid);
        }
        60%,
        100% {
            opacity: 1;
            transform: translate(0.7rem, calc(5rem + var(--notification-stack-y))) scale(1.04) rotate(0deg);
            background: var(--notification-stack-bg);
            border-color: transparent;
            box-shadow: 0 0 0 transparent;
        }
    }

    :global(:root:not([data-theme="light"])) .notification-card {
        --notification-stack-bg: #111319;
        --notification-stack-title: #fff;
        --notification-stack-muted: #8e9199;
    }

    @media (max-width: 640px) {
        .notification-card {
            width: 100%;
        }
        .notification-card-1 {
            --notification-x: -1rem;
            --notification-y: 0;
        }
        .notification-card-2 {
            --notification-x: -7rem;
            --notification-y: 7rem;
        }
        .notification-card-3 {
            --notification-x: -1rem;
            --notification-y: 13.5rem;
        }

        @keyframes notification-sequence {
            0%,
            28% {
                opacity: 1;
                transform: translate(1.9rem, calc(0.25rem + var(--notification-start-y))) scale(1) rotate(0deg);
            }
            36%,
            52% {
                opacity: 1;
                transform: translate(var(--notification-x), var(--notification-y)) scale(1) rotate(var(--notification-rotate));
                background: var(--surface-solid);
            }
            60%,
            100% {
                opacity: 1;
                transform: translate(1.9rem, calc(5rem + var(--notification-stack-y))) scale(1.04) rotate(0deg);
                background: var(--notification-stack-bg);
                border-color: transparent;
                box-shadow: 0 0 0 transparent;
            }
        }
    }
</style>
