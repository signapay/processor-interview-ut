import { NextResponse } from "next/server";

export async function POST() {
  const apiKey = process.env.API_KEY;
  const backendUrl = process.env.BACKEND_URL;

  const url = `${backendUrl}/api/reset`;

  try {
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "x-api-key": apiKey as string,
      },
      cache: "no-store",
    });

    if (!response.ok) {
      throw new Error("Reset failed");
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error("Reset error:", error);
    return NextResponse.json(
      { error: "Failed to reset system" },
      { status: 500 }
    );
  }
}
