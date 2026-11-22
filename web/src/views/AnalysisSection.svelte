<script lang="ts">
    import Phone from "../components/Phone.svelte";
    import LogConsole from "../components/LogConsole.svelte";
    import RollingNumber from "../components/RollingNumber.svelte";
    import { screenshot } from "../lib/screenshots";
    import { tokens } from "../lib/text";

    export let t: any;
    export let language: "zh" | "en" = "zh";
    export let theme: "light" | "dark" = "light";
    export let active = false;
</script>

<section
    class="snap-section analysis-section"
    class:section-active={active}
    id="insights"
    data-section="2">
    <div class="ambient ambient-two"></div>
    <div class="section-grid analysis-grid">
        <div class="copy-block">
            <p class="eyebrow">
                <span class="eyebrow-line"></span>
                {t.analysisKicker}
            </p>
            <h2>
                {#each tokens(t.analysisTitle, language) as token, index}
                    <span
                        class="reveal-word"
                        style={`--word-delay:${index * 32}ms`}>{token}</span>
                {/each}
            </h2>
            <p class="lede">
                {#each tokens(t.analysisBody, language) as token, index}
                    <span
                        class="reveal-word"
                        style={`--word-delay:${index * 24}ms`}>{token}</span>
                {/each}
            </p>
            <div class="feature-pair">
                <div>
                    <strong>
                        <RollingNumber
                            value={t.insightStat}
                            {active} />
                    </strong>
                    <span class="feature-label">{t.insightStatLabel}</span>
                </div>
                <div>
                    <strong>
                        <RollingNumber
                            value="0"
                            {active} />
                    </strong>
                    <span class="feature-label">
                        {language === "zh" ? "云端依赖" : "cloud dependencies"}
                    </span>
                </div>
            </div>
        </div>

        <div class="analysis-visual">
            <LogConsole
                title={t.liveLog}
                rows={t.logRows.concat(t.logRows).concat(t.logRows).concat(t.logRows)}
                {active} />
            <Phone
                variant="log"
                src={screenshot("log", language, theme)}
                alt={`${t.appName} log screenshot`} />
        </div>
    </div>
</section>

<style>
    .analysis-grid {
        grid-template-columns: minmax(18.75rem, 0.8fr) minmax(28.75rem, 1.2fr);
    }

    .analysis-visual {
        min-height: 34.375rem;
        position: relative;
        display: grid;
        place-items: center;
    }

    .ambient-two {
        background: #4e9f93;
        opacity: 0.15;
        --ambient-x: -1.25rem;
        --ambient-y: 0.75rem;
        animation-delay: -3s;
    }

    .analysis-section > .ambient-two {
        bottom: 0;
        right: 5%;
    }

    @media (max-width: 640px) {
        .analysis-visual {
            min-height: 24.375rem;
            order: 0;
        }

        .analysis-grid {
            grid-template-columns: 1fr;
        }

        .analysis-section .copy-block {
            order: 1;
        }
    }
</style>
