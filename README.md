# S50 Polar Align

An Android-first, offline polar-alignment prototype for the Seestar S50. Version 0.1 contains a deterministic star detector, tangent-plane similarity solver, three-position mount-axis fit, a no-network demo workflow, and an optional OpenGL ES 3.1 luminance compute stage. It uses no AI and sends no image data off-device.

## Open and run

1. Open this directory in Android Studio (JDK 17, Android SDK 35).
2. Let Android Studio supply/download the Gradle wrapper if prompted, then run `app` on an API 29+ device or emulator.
3. Tap **Run simulated 3-frame alignment**. The expected result is an axis error of about `0.22°`, with azimuth left `0.18°` and altitude up `0.12°`.

Run the platform-independent core verification with:

```sh
./scripts/test-core.sh
```

Run the Android instrumentation check with Android Studio, or with Gradle once a wrapper is present:

```sh
./gradlew connectedDebugAndroidTest
```

## Architecture and current boundary

- `StarDetector`: background/noise estimate, local maxima, 5×5 flux-weighted centroids.
- `TriangleMatcher`: scale/rotation-independent offline geometric matching against an in-memory catalog tile.
- `SimilaritySolver`: least-squares image-to-tangent-plane transform and residual.
- `PolarAlignment`: circle fit through three or more solved optical-axis positions.
- `GpuPreprocessor`: optional GLES 3.1 compute shader for per-pixel luminance conversion. This is the kind of dense, independent arithmetic a phone GPU helps with. Centroiding and the small matrix fits stay on CPU because dispatch/readback overhead would dominate.
- `S50SnapshotClient`: a configurable JPEG snapshot adapter. It deliberately does not bake in an undocumented S50 URL or protocol.

The repository currently validates the mathematical workflow with synthetic, exactly known inputs. The geometric matcher is present, but a production build still needs a licensed offline star-catalog asset packaged into sky tiles. That catalog must cover the intended polar region to the S50 limiting magnitude and should be generated reproducibly from a cited catalog rather than invented. The core APIs are separated so catalog tiles can feed `TriangleMatcher` without changing the UI or polar-axis fit.

## S50 feed and refresh-rate assumptions

The achievable refresh rate is controlled mainly by how the S50 exposes frames, not the phone GPU:

| Input assumption | Realistic feedback cadence | Limiting factor |
|---|---:|---|
| New single exposure JPEG available over Wi-Fi | Exposure time + roughly 0.2–1.0 s | S50 capture/export and Wi-Fi transfer |
| Preview JPEG refreshed by an endpoint | Roughly 0.5–2 Hz | Endpoint polling and JPEG decode |
| RTSP/MJPEG preview stream is available | Potentially 2–10 Hz display; solve 0.5–2 Hz | Stream access, compression, solve cadence |
| Files appear only after the S50 stacking cycle | One correction per completed saved frame | S50 firmware workflow |

These are engineering ranges, not claims about a stable public S50 API. Full-resolution blind solving every video frame is unnecessary for polar alignment. A practical loop decodes each available frame, performs GPU luminance/downsampling when worthwhile, solves at 0.5–2 Hz, and renders interpolated UI feedback at display rate. Once a measured S50 transport is selected, record capture latency, transfer latency, decode time, star detection time, solve time, and dropped frames separately.

## GitHub readiness

The tree includes a focused `.gitignore`, no credentials, no generated binaries, and no dependence on a cloud service. Before the first public release, add a license and choose whether S50 transport code may rely on reverse-engineered endpoints. Suggested initial tag: `v0.1.0-prototype`.

## Safety and accuracy

Do not adjust the mount from a low-confidence result. Require at least three positions separated substantially in right ascension, reject fits with large residuals, and verify the correction with another solve sequence. The numerical result is only as accurate as the plate solutions and the S50 optical-axis stability.
