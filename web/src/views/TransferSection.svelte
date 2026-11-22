<script lang="ts">
    import Phone from "../components/Phone.svelte";
    import { screenshot } from "../lib/screenshots";
    import { tokens } from "../lib/text";

    export let t: any;
    export let language: "zh" | "en" = "zh";
    export let theme: "light" | "dark" = "light";
    export let active = false;
</script>

<section
    class="snap-section transfer-section"
    class:section-active={active}
    id="transfer"
    data-section="3">
    <div class="ambient ambient-three"></div>
    <div class="section-grid transfer-grid">
        <div class="transfer-visual">
            <div class="nearby-orbit orbit-one"></div>
            <div class="nearby-orbit orbit-two"></div>
            <div class="transfer-beam">
                <span class="beam-dot"></span>
                <span class="beam-dot"></span>
                <span class="beam-dot"></span>
            </div>
            <Phone
                variant="transfer"
                src={screenshot("transfer", language, theme)}
                alt={`${t.appName} transfer screenshot`} />
        </div>

        <div class="copy-block transfer-copy">
            <p class="eyebrow"><span class="eyebrow-line"></span>{t.transferKicker}</p>
            <h2>
                {#each tokens(t.transferTitle, language) as token, index}
                    <span
                        class="reveal-word"
                        style={`--word-delay:${index * 32}ms`}>
                        {token}
                    </span>
                {/each}
            </h2>
            <p class="lede">
                {#each tokens(t.transferBody, language) as token, index}
                    <span
                        class="reveal-word"
                        style={`--word-delay:${index * 24}ms`}>
                        {token}
                    </span>
                {/each}
            </p>
            <div class="nearby-caption">
                <span class="nearby-glyph">⌁</span>
                <div>
                    <b>{t.nearby}</b>
                    <span>{language === "zh" ? "跨设备导出与备份" : "Cross-device export & backup"}</span>
                </div>
            </div>
        </div>
    </div>
</section>

<style>
    .transfer-grid {
        grid-template-columns: minmax(28.75rem, 1.15fr) minmax(18.75rem, 0.85fr);
    }

    .ambient-three {
        left: 6%;
        bottom: 0;
        background: #7e58d6;
        opacity: 0.17;
        --ambient-x: 1rem;
        --ambient-y: -1.25rem;
        animation-delay: -6s;
    }

    .transfer-visual {
        min-height: 29.375rem;
        position: relative;
        display: grid;
        place-items: center;
    }

    .nearby-orbit {
        position: absolute;
        bottom: 0;
        left: 12%;
        border: 1px solid var(--line);
        border-radius: 50%;
        width: 30.625rem;
        height: 11.875rem;
        transform: rotate(-18deg);
    }

    .nearby-orbit.orbit-two {
        width: 30rem;
        height: 18.125rem;
        transform: rotate(37deg);
        border-color: rgba(145, 169, 255, 0.28);
    }

    .nearby-glyph {
        color: var(--accent-strong);
        font-size: 2.3125rem;
        line-height: 0.7;
        display: block;
        transform: rotate(-20deg);
    }

    .transfer-beam {
        width: 75%;
        height: 0.0625rem;
        position: absolute;
        bottom: 25%;
        left: 0;
        background: linear-gradient(90deg, transparent, var(--accent), transparent);
        transform: rotate(-21deg);
        z-index: 1;
    }

    .beam-dot {
        width: 0.375rem;
        height: 0.375rem;
        border-radius: 50%;
        background: var(--accent-strong);
        position: absolute;
        top: -0.1563rem;
        left: 0;
        box-shadow: 0 0 0.75rem var(--accent);
        animation: beam-move 2.4s linear infinite;
    }

    .beam-dot:nth-child(2) {
        animation-delay: 0.8s;
    }
    .beam-dot:nth-child(3) {
        animation-delay: 1.6s;
    }

    .nearby-caption {
        display: flex;
        gap: 0.75rem;
        align-items: center;
        margin-top: 2.75rem;
        padding-top: 1.1875rem;
        border-top: 0.0625rem solid var(--line);
        max-width: 18.75rem;
    }

    .nearby-caption .nearby-glyph {
        font-size: 1.625rem;
    }
    .nearby-caption div {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }
    .nearby-caption b {
        font-size: 0.75rem;
    }
    .nearby-caption span:last-child {
        color: var(--muted);
        font-size: 0.625rem;
    }

    @keyframes beam-move {
        from {
            left: 0;
        }
        to {
            left: calc(100% - 0.375rem);
        }
    }

    @media (max-width: 640px) {
        .transfer-visual {
            min-height: 24.375rem;
            order: 0;
        }

        .transfer-copy {
            order: 1;
        }

        .nearby-orbit {
            width: 21.875rem;
        }

        .transfer-grid {
            grid-template-columns: 1fr;
        }
    }
</style>
