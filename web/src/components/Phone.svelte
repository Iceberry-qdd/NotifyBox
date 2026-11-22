<script lang="ts">
    export let src = "";
    export let alt = "";
    export let variant: "hero-device" | "statistic" | "log" | "transfer" = "statistic";
</script>

<div class={`phone phone-${variant}`}>
    <img {src} {alt} />
</div>

<style>
    .phone {
        position: relative;
        overflow: hidden;
        aspect-ratio: 1440 / 3200;
        border: 0.35rem solid #1b2230;
        background: #111722;
        box-shadow:
            var(--shadow),
            0 0 0 0.0625rem rgba(255, 255, 255, 0.14);
    }

    .phone img {
        width: 100%;
        height: 100%;
        display: block;
        object-fit: cover;
        object-position: top center;
    }

    .phone-hero-device {
        width: 100%;
        height: 100%;
        border-radius: 1.75rem;
        transform: rotate(5deg);
    }

    .phone-statistic {
        position: relative;
        width: 100%;
        height: 100%;
        border-radius: 1.8125rem;
        transform: rotate(7deg);
        z-index: 2;
    }

    .phone-log {
        position: absolute;
        width: clamp(14.6875rem, 23vw, 19.0625rem);
        height: auto;
        left: 54%;
        top: clamp(4.5rem, 8svh, 6rem);
        border-radius: 1.8125rem;
        transform: rotate(6deg);
        opacity: 0.94;
        z-index: 2;
    }

    .phone-transfer {
        position: absolute;
        left: 25%;
        width: clamp(14.6875rem, 23vw, 19.0625rem);
        height: auto;
        right: 18%;
        top: clamp(4.5rem, 8svh, 6rem);
        border-radius: 1.5625rem;
        transform: rotate(7deg);
        z-index: 1;
    }

    @media (max-width: 640px) {
        .phone-log {
            width: 11.875rem;
            right: -1%;
            top: clamp(3.75rem, 7svh, 4.75rem);
            position: unset;
        }

        .phone-transfer {
            width: 11.875rem;
            right: 16%;
            top: clamp(3.75rem, 7svh, 4.75rem);
            position: unset;
        }
    }
</style>
