<script lang="ts">
    import Phone from "../components/Phone.svelte";
    import RollingNumber from "../components/RollingNumber.svelte";
    import StatsChartCard from "../components/StatsChartCard.svelte";
    import { screenshot } from "../lib/screenshots";
    import { tokens } from "../lib/text";

    export let t: any;
    export let language: "zh" | "en" = "zh";
    export let theme: "light" | "dark" = "light";
    export let active = false;
</script>

<section
    class="snap-section statistics-section"
    class:section-active={active}
    id="statistics"
    data-section="1">
    <div class="ambient ambient-statistics"></div>
    <div class="section-grid analysis-grid statistics-grid">
        <div class="statistics-visual">
            <div class="statistics-phone-stage">
                <StatsChartCard
                    type="donut"
                    {active} />
                <StatsChartCard
                    type="bars"
                    {active} />
                <Phone
                    variant="statistic"
                    src={screenshot("statistic", language, theme)}
                    alt={`${t.appName} statistic screenshot`} />
            </div>
        </div>

        <div class="copy-block">
            <p class="eyebrow"><span class="eyebrow-line"></span>{t.statisticsKicker}</p>
            <h2>
                {#each tokens(t.statisticsTitle, language) as token, index}
                    <span
                        class="reveal-word"
                        style={`--word-delay:${index * 32}ms`}>
                        {token}
                    </span>
                {/each}
            </h2>
            <p class="lede">
                {#each tokens(t.statisticsBody, language) as token, index}<span
                        class="reveal-word"
                        style={`--word-delay:${index * 24}ms`}>{token}</span
                    >{/each}
            </p>
            <div class="feature-pair">
                <div>
                    <strong>
                        <RollingNumber
                            value={t.statisticsTotal}
                            {active} />
                    </strong>
                    <span class="feature-label">{t.statisticsTotalLabel}</span>
                </div>
                <div>
                    <strong>
                        <RollingNumber
                            value="4"
                            {active} />
                    </strong>
                    <span class="feature-label">
                        {language === "zh" ? "通知类别" : "notification groups"}
                    </span>
                </div>
            </div>
        </div>
    </div>
</section>

<style>
    .statistics-grid {
        grid-template-columns: minmax(28.75rem, 1.2fr) minmax(18.75rem, 0.8fr);
    }

    .statistics-visual {
        min-height: 34.375rem;
        position: relative;
        display: grid;
        place-items: center;
        margin-left: 8rem;
    }

    .statistics-phone-stage {
        position: absolute;
        top: clamp(4.5rem, 8svh, 6rem);
        width: clamp(14.6875rem, 23vw, 19.0625rem);
        height: auto;
        aspect-ratio: 1440 / 3200;
        transform: translateX(-50%);
        overflow: visible;
        z-index: 2;
    }

    .ambient-statistics {
        background: #f28fb1;
        opacity: 0.2;
        --ambient-x: -1rem;
        --ambient-y: 1.25rem;
        animation-delay: -4s;
    }

    .statistics-section > .ambient-statistics {
        left: 5%;
        bottom: 0;
    }

    @media (max-width: 640px) {
        .statistics-visual {
            min-height: 24.375rem;
            order: 0;
            margin-left: 0;
        }

        .statistics-section .copy-block {
            order: 1;
        }

        .statistics-phone-stage {
            width: 11.875rem;
            height: auto;
            top: clamp(3.75rem, 7svh, 4.75rem);
            position: unset;
            transform: translateX(0);
        }

        .statistics-grid {
            grid-template-columns: 1fr;
        }
    }
</style>
