package com.journaldev.servlet.async;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/AsyncLongRunningServlet", asyncSupported = true)
public class AsyncLongRunningServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		System.out.println("AsyncLongRunningServlet Start::Name="
				+ Thread.currentThread().getName() + "::ID="
				+ Thread.currentThread().getId());

		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);

		String time = request.getParameter("time");
		Random random = new Random();
		int secs = random.nextInt(10000);
		int timeoutSecs = random.nextInt(10000);
		System.out.println("secs="+secs+"|timeoutSecs="+timeoutSecs);
		AsyncContext asyncCtx = request.startAsync();
		asyncCtx.addListener(new AppAsyncListener());
		asyncCtx.setTimeout(timeoutSecs);

		ThreadPoolExecutor executor = (ThreadPoolExecutor) request
				.getServletContext().getAttribute("executor");

		executor.execute(new AsyncRequestProcessor(asyncCtx, secs));
		long endTime = System.currentTimeMillis();
		System.out.println("AsyncLongRunningServlet End::Name="
				+ Thread.currentThread().getName() + "::ID="
				+ Thread.currentThread().getId() + "::Time Taken="
				+ (endTime - startTime) + " ms.");
	}

}
