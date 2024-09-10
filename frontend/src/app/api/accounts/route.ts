import { NextResponse } from "next/server";

export async function GET() {
  const apiKey = process.env.API_KEY;
  const backendUrl = process.env.BACKEND_URL;

  const url = `${backendUrl}/api/accounts`;

  try {
    const response = await fetch(url, {
      headers: {
        "x-api-key": apiKey as string,
      },
      cache: "no-store",
    });

    if (!response.ok) {
      throw new Error("Failed to fetch accounts");
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error("Accounts fetch error:", error);
    return NextResponse.json(
      { error: "Failed to fetch accounts" },
      { status: 500 }
    );
  }
}
